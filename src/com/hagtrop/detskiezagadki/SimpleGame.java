package com.hagtrop.detskiezagadki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleGame extends FragmentActivity implements LoaderCallbacks<Cursor>, OnClickListener, NoticeDialogListener {
	TextView questionTV, answerTV, progressTV, levelTV, timeTV, attemptsTV;
	Button checkBtn;
	LinearLayout answerLayout;
	
	private static final int ARRAY_LOADER = 0;
	private static final int QUESTION_LOADER = 1;
	private static final char[] RUS_ALPHABET = new char[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
	private Random random = new Random();
	private ArrayList<QueStatus> queStatusList;
	private SQLiteDatabase database;
	private ArrayList<Button> lettersBtns;
	private AnswerButtonsArray answerBtns;
	private char[] answerLetters;
	private int focusBtnNum = 0;
	//private Question currentQuestion;
	private BaseHelper baseHelper;
	private Handler handler;
	private MyTimer timer;
	private GameInfo gameInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_simple_game);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			gameInfo = new GameInfo(
					extras.getBoolean("useTimer"),
					extras.getBoolean("useAttemptsLimit"));
		}
		else gameInfo = new GameInfo(false, false);
		
		progressTV = (TextView) findViewById(R.id.a1_progressTV);
		levelTV = (TextView) findViewById(R.id.a1_levelTV);
		attemptsTV = (TextView) findViewById(R.id.a1_attemptsTV);
		timeTV = (TextView) findViewById(R.id.a1_timeTV);
		
		//Отображаем/скрываем счётчик оставшихся попыток
		if(gameInfo.USE_ATTEMPTS_LIMIT) attemptsTV.setVisibility(View.VISIBLE);
		else attemptsTV.setVisibility(View.GONE);
		
		//Отображаем таймер, если выбран режим с таймером
		if(gameInfo.USE_TIMER) timeTV.setVisibility(View.VISIBLE);
		else timeTV.setVisibility(View.GONE);
		
		questionTV = (TextView) findViewById(R.id.a1_questionTV);
		answerTV = (TextView) findViewById(R.id.a1_answerTV);
		checkBtn = (Button) findViewById(R.id.a1_checkBtn);
		checkBtn.setOnClickListener(this);
		answerLayout = (LinearLayout) findViewById(R.id.a1_answerLayout);
		
		//Объединяем в массив пустые кнопки ответа
		answerBtns = new AnswerButtonsArray();
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn1));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn2));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn3));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn4));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn5));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn6));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn7));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn8));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn9));
		answerBtns.add((Button) findViewById(R.id.a1_answerBtn10));
		answerBtns.setOnClickListener(new AnswerLettersOnClickListener());
		
		//Объединяем в массив кнопки с буквами
		lettersBtns = new ArrayList<Button>();
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn1));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn2));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn3));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn4));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn5));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn6));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn7));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn8));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn9));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn10));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn11));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn12));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn13));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn14));
		lettersBtns.add((Button) findViewById(R.id.a1_letterBtn15));
		LettersOnClickListener lettersOnClickListener = new LettersOnClickListener();
		for(Button btn : lettersBtns) btn.setOnClickListener(lettersOnClickListener);
				
		baseHelper = BaseHelper.getInstance(this);
		//Создаём таблицу новой игры, если требуется 
		if(extras.getBoolean("createNewGame")) baseHelper.newGame(StartMenuActivity.SIMPLE_GAME, gameInfo.USE_ATTEMPTS_LIMIT, gameInfo.USE_TIMER);
		
		database = baseHelper.getWritableDatabase();
		getSupportLoaderManager().initLoader(ARRAY_LOADER, null, this);
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(timer);
		updateGame();
	}



	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Bundle params;
		switch(loaderID){
		case ARRAY_LOADER:
			return new MyCursorLoader(this, baseHelper, MyCursorLoader.GET_GAME_TABLE, null);
		case QUESTION_LOADER:
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, baseHelper, MyCursorLoader.GET_QUESTION, params);
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		//Сохраняем в ArrayList параметры и характеристики вопросов для дальнейшей сортировки
		case ARRAY_LOADER:
			if(cursor.moveToFirst()){
				queStatusList = new ArrayList<QueStatus>();
				long queTimeSpent;
				int queStatus;
				int queIndex = -1;
				boolean nextFound = false;
				do{
					queIndex++;
					gameInfo.addAttemptsSpent(cursor.getInt(cursor.getColumnIndex("attempts")));
					queTimeSpent = cursor.getLong(cursor.getColumnIndex("time"));
					gameInfo.setTimePassed(gameInfo.getTimePassed() + queTimeSpent);
					queStatus = cursor.getInt(cursor.getColumnIndex("status"));
					//Определяем номер первой неотвеченной загадки
					if(!nextFound && queStatus == 0){
						gameInfo.setQueIndex(queIndex);
						nextFound = true;
					}
					queStatusList.add(new QueStatus(
							cursor.getInt(cursor.getColumnIndex("question_id")),
							queStatus,
							queTimeSpent));
					
				} while(cursor.moveToNext());
				
				gameInfo.setAttemptsLimit(queStatusList.size()*2);
				attemptsTV.setText("Попыток: " + String.valueOf(gameInfo.getAttemptsLimit() - gameInfo.getAttemptsSpent()));
				gameInfo.setTimeLimit(10 * 1000 * queStatusList.size());
			}
			printArray(queStatusList);
			loadQuestion(queStatusList.get(gameInfo.getQueIndex()).getId());
			break;
		//Извлекаем вопрос и ответ
		case QUESTION_LOADER:
			if(cursor.moveToFirst()){
				gameInfo.newQue(
						cursor.getString(cursor.getColumnIndex("question")).replace("\\n", "\n"), 
						cursor.getString(cursor.getColumnIndex("answer")).trim().toUpperCase(new Locale("ru")),
						cursor.getInt(cursor.getColumnIndex("level")));
				
				Log.d("mLog", "index = " + gameInfo.getQueIndex());
				
				progressTV.setText(getString(R.string.a1_progressTV) + " " + (gameInfo.getQueIndex()+1) + "/" + queStatusList.size());
				levelTV.setText(getString(R.string.a1_levelTV) + " " + gameInfo.getQueLevel());
				questionTV.setText(gameInfo.getQuestion());
				answerTV.setText(gameInfo.getAnswer());
				answerLetters = gameInfo.getAnswer().toCharArray();
				answerBtns.setVisible(answerLetters.length);
				
				//Создаём массив вариантов букв
				char[] allLetters = new char[15];
				System.arraycopy(answerLetters, 0, allLetters, 0, answerLetters.length);
				for(int i=answerLetters.length; i < allLetters.length; i++){
					char letter = RUS_ALPHABET[random.nextInt(33)];
					allLetters[i] = letter;
				}
				//Перемешиваем массив букв
				ArrayShuffle.reshuffle(allLetters);
				//Выводим буквы на кнопки
				for(int i=0; i<lettersBtns.size(); i++){
					lettersBtns.get(i).setText(String.valueOf(allLetters[i]));
				}
				
				//Отображаем все кнопки
				for(Button btn : lettersBtns){
					btn.setVisibility(View.VISIBLE);
				}
				//Очищаем кнопки ответа
				answerBtns.clearText();
				
				//Устанавливаем фокус в позицию 0
				focusBtnNum = 0;
				
				//Запускаем таймер
				Handler.Callback hCallback = new Handler.Callback() {	
					@Override
					public boolean handleMessage(Message msg) {
						if(msg.what == 0 && gameInfo.USE_TIMER){
							Log.d("mLog", "TIME LEFT 0");
							showTimeIsOverMessage();
						}
						return false;
					}
				};
				handler = new Handler(hCallback);
				timer = new MyTimer(handler, timeTV, gameInfo);
				handler.postDelayed(timer, 1000);
				
				break;
			}
		default: break;
		}
	}
	
	void showTimeIsOverMessage(){
		FragmentManager fManager = getSupportFragmentManager();
		TimeIsOverDialog dialog = new TimeIsOverDialog();
		dialog.show(fManager, "time_is_over_dialog");
	}
	
	void showNoAttemptsDialog(){
		//Выводим сообщение о том, что попытки закончились
		FragmentManager fManager = getSupportFragmentManager();
		NoAttemptsDialog noAttemptsDialog = new NoAttemptsDialog();
		noAttemptsDialog.show(fManager, "no_attempts_dialog");
	}
	
	void showEndGameDialog(){
		FragmentManager fManager = getSupportFragmentManager();
		GoodGameDialog gameoverDialog = new GoodGameDialog();
		gameoverDialog.show(fManager, "gameover_dialog");
	}
	
	void endGame(){
		gameInfo.setGameOver(true);
		baseHelper.deleteOldGames(database);
		baseHelper.close();
		finish();
	}
	
	//Асинхронно загружаем вопрос, используя LoaderManager
	private void loadQuestion(int queId){
		Bundle bundle = new Bundle();
		bundle.putInt("queId", queId);
		getSupportLoaderManager().restartLoader(QUESTION_LOADER, bundle, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub 
		
	}
	
	private void printArray(ArrayList<QueStatus> mArray){
		for(QueStatus qStatus : mArray){
			Log.d("mLog", "queId=" + qStatus.getId() + " queStatus=" + qStatus.getStatus());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a1_checkBtn:
			if(answerBtns.getPlayerAnswer().equals(gameInfo.getAnswer())){
				gameInfo.goodAnswer(true);
				//Вывод следующего вопроса в методе onDialogDismiss
			}
			else{
				gameInfo.goodAnswer(false);
			}
			handler.removeCallbacks(timer);
			updateGame();
			FragmentManager fManager = getSupportFragmentManager();
			TrueFalseDialog dialog = new TrueFalseDialog(gameInfo.goodAnswer());
			dialog.show(fManager, "answer_result_dialog");
			break;
		default: break;
		}
	}
	
	//Обработчик для массива букв
	class LettersOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			//Скрываем нажатую кнопку и пишем её букву на кнопке в строке ответа
			btn.setVisibility(View.INVISIBLE);
			answerBtns.setLetter(focusBtnNum, btn);
			//Ищем первую пустую позицию в строке ответа и перемещаем фокус на неё
			int emtyBtnNum = answerBtns.getFirstEmptyBtn();
			if(emtyBtnNum > -1){
				focusBtnNum = emtyBtnNum;
				checkBtn.setEnabled(false);
			}
			else checkBtn.setEnabled(true);
		}
		
	}
	
	//Обработчик для кнопок строки ответа
	class AnswerLettersOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			//Удаляем букву с кнопки в строке ответа и устанавливаем фокус в эту позицию
			answerBtns.deleteLetter(btn);
			focusBtnNum = answerBtns.indexOf(btn);
			if(answerBtns.getFirstEmptyBtn() > -1) checkBtn.setEnabled(false);
		}
		
	}

	@Override
	public void onDialogDismiss(DialogFragment dialog, String dialogType) {
		if(dialogType.equals(TrueFalseDialog.DIALOG_TYPE)){
			gameInfo.addQueAttemptsSpent(1);
			attemptsTV.setText("Попыток: " + gameInfo.getAttemptsRemaining());
			if(gameInfo.USE_ATTEMPTS_LIMIT && gameInfo.getAttemptsRemaining() < 1 && !(gameInfo.getQueIndex() == queStatusList.size()-1 && gameInfo.goodAnswer())){
				showNoAttemptsDialog();
			}
			else if(gameInfo.goodAnswer()){
				//Загружаем следующий вопрос
				if(gameInfo.getQueIndex() < queStatusList.size()-1){
					gameInfo.setQueIndex(gameInfo.getQueIndex()+1);
					loadQuestion(queStatusList.get(gameInfo.getQueIndex()).getId());
					checkBtn.setEnabled(false);
					Log.d("mLog", "currentQueIndex=" + gameInfo.getQueIndex());
				}
				else{
					showEndGameDialog();
				}
			}
			else{
				handler.postDelayed(timer, 1000);
			}
		}
		else if(dialogType.equals(NoAttemptsDialog.DIALOG_TYPE)){
			endGame();
		}
		else if(dialogType.equals(TimeIsOverDialog.DIALOG_TYPE)){
			endGame();
		}
		else if(dialogType.equals(GoodGameDialog.DIALOG_TYPE)){
			//Возвращаемся в меню выбора типа игры
			endGame();
		}
	}
	
	private void updateGame(){
		if(!gameInfo.gameOver()){
		baseHelper.updateGame(
				queStatusList.get(gameInfo.getQueIndex()).getId(), 
				gameInfo.goodAnswer() ? 1 : 0, 
				gameInfo.getQueAttemptsSpent(),
				gameInfo.getQueTimePassed());
		}
	}
}