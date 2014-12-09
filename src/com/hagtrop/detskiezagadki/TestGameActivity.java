package com.hagtrop.detskiezagadki;

import java.util.ArrayList;
import java.util.Locale;

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
import android.view.View.OnLayoutChangeListener;
import android.widget.Button;
import android.widget.TextView;

public class TestGameActivity extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor>, NoticeDialogListener{
	TextView progressTV, levelTV, attemptsTV, questionTV, answerTV, timerTV;
	
	ArrayList<Button> answerBtns;
	
	private static final int ARRAY_LOADER = 0;
	private static final int QUESTION_LOADER = 1;
	private static final int VARIANTS_LOADER = 2;
	private BaseHelper baseHelper;
	private SQLiteDatabase database;
	private ArrayList<QueStatus> queStatusList;
	//private Question currentQuestion;
	private String[] variants;
	//private int attemptsRemaining;
	
	private Handler handler;
	private MyTimer timer;
	private GameInfo gameInfo;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a2_test_game);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			gameInfo = new GameInfo(
					extras.getBoolean("useTimer"),
					extras.getBoolean("useAttemptsLimit"));
		}
		else gameInfo = new GameInfo(false, false);
		
		progressTV = (TextView) findViewById(R.id.a2_progressTV);
		levelTV = (TextView) findViewById(R.id.a2_levelTV);
		attemptsTV = (TextView) findViewById(R.id.a2_attemptsTV);
		questionTV = (TextView) findViewById(R.id.a2_questionTV);
		answerTV = (TextView) findViewById(R.id.a2_answerTV);
		timerTV = (TextView) findViewById(R.id.a2_timerTV);
		
		//Отображаем/скрываем счётчик оставшихся попыток
		if(gameInfo.USE_ATTEMPTS_LIMIT) attemptsTV.setVisibility(View.VISIBLE);
		else attemptsTV.setVisibility(View.GONE);
		
		//Отображаем таймер, если выбран режим с таймером
		if(gameInfo.USE_TIMER) timerTV.setVisibility(View.VISIBLE);
		else timerTV.setVisibility(View.GONE);
		
		answerBtns = new ArrayList<Button>();
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn1));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn2));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn3));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn4));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn5));
		answerBtns.add((Button) findViewById(R.id.a2_answerBtn6));
		for(Button btn : answerBtns){
			btn.setOnClickListener(new AnswerBtnsOnClickListener());
		}
		
		//Выравниваем кнопки по ширине
		answerBtns.get(5).addOnLayoutChangeListener(new OnLayoutChangeListener() {
			int w0, w1;
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				Log.d("mLog", "onLayoutChange");
				w0 = answerBtns.get(0).getWidth();
				w1 = answerBtns.get(1).getWidth();
				if(w1 > w0) answerBtns.get(0).setWidth(w1);
				else if(w0 > w1) answerBtns.get(1).setWidth(w0);
			}
		});
		
		baseHelper = BaseHelper.getInstance(this);
		
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
	protected void onResume() {
		super.onResume();
		if(handler != null) handler.postDelayed(timer, 1000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private class AnswerBtnsOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			gameInfo.addQueAttemptsSpent(1);
			Button btn = (Button) v;
			btn.setEnabled(false);
			//attemptsRemaining--;
			//attemptsTV.setText(getString(R.string.a2_attemptsTV) + " " + String.valueOf(gameInfo.getAttemptsLimit() - gameInfo.getAttemptsSpent()));
			if(btn.getText().equals(gameInfo.getAnswer())){
				gameInfo.goodAnswer(true);
			}
			else{
				gameInfo.goodAnswer(false);
			}
			handler.removeCallbacks(timer);
			updateGame();
			FragmentManager fManager = getSupportFragmentManager();
			TrueFalseDialog dialog = new TrueFalseDialog(gameInfo.goodAnswer());
			dialog.show(fManager, "answer_result_dialog");
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Log.d("mLog", "onCreateLoader");
		Bundle params;
		switch(loaderID){
		case ARRAY_LOADER:
			return new MyCursorLoader(this, baseHelper, MyCursorLoader.GET_GAME_TABLE, null);
		case QUESTION_LOADER:
			Log.d("mLog", "onCreateLoader, queId=" + bundle.getInt("queId"));
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, baseHelper, MyCursorLoader.GET_QUESTION, params);
		case VARIANTS_LOADER:
			params = new Bundle();
			params.putInt("questionId", bundle.getInt("queId"));
			return new MyCursorLoader(this, baseHelper, MyCursorLoader.GET_VARIANTS, params);
		default: return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
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
		case QUESTION_LOADER:
			if(cursor.moveToFirst()){
				gameInfo.newQue(
						cursor.getString(cursor.getColumnIndex("question")).replace("\\n", "\n"), 
						cursor.getString(cursor.getColumnIndex("answer")).trim().toUpperCase(new Locale("ru")),
						cursor.getInt(cursor.getColumnIndex("level")));
				
				//Log.d("mLog", currentQuestion.getQuestion());
				//Log.d("mLog", currentQuestion.getAnswer());
				progressTV.setText(getString(R.string.a1_progressTV) + " " + (gameInfo.getQueIndex()+1) + "/" + queStatusList.size());
				levelTV.setText(getString(R.string.a1_levelTV) + " " + gameInfo.getQueLevel());
				questionTV.setText(gameInfo.getQuestion());
				answerTV.setText(gameInfo.getAnswer());
				Log.d("mLog", "QUESTION_LOADER");
				
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
				timer = new MyTimer(handler, timerTV, gameInfo);
				handler.postDelayed(timer, 1000);
				
				break;
			}
		case VARIANTS_LOADER:
			if(cursor.moveToFirst()){
				variants = new String[6];
				int i = 0;
				do{
					variants[i] = cursor.getString(cursor.getColumnIndex("answer")).toUpperCase(new Locale("ru"));
					i++;
				}
				while(cursor.moveToNext());
				ArrayShuffle.reshuffle(variants);
				//Выводим слова на кнопки
				for(int j=0; j<answerBtns.size(); j++){
					answerBtns.get(j).setEnabled(true);
					answerBtns.get(j).setText(variants[j]);	
				}
			}
			break;
		default: break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	//Асинхронно загружаем вопрос, используя LoaderManager
	private void loadQuestion(int queId){
		Bundle bundle = new Bundle();
		bundle.putInt("queId", queId);
		getSupportLoaderManager().restartLoader(QUESTION_LOADER, bundle, this);
		getSupportLoaderManager().restartLoader(VARIANTS_LOADER, bundle, this);
	}
	
	void endGame(){
		gameInfo.setGameOver(true);
		baseHelper.deleteOldGames(database);
		baseHelper.close();
		finish();
	}

	@Override
	public void onDialogDismiss(DialogFragment dialog, String dialogType) {
		if(dialogType.equals(TrueFalseDialog.DIALOG_TYPE)){
			attemptsTV.setText("Попыток: " + gameInfo.getAttemptsRemaining());
			if(gameInfo.USE_ATTEMPTS_LIMIT && gameInfo.getAttemptsRemaining() < 1 && !(gameInfo.getQueIndex() == queStatusList.size()-1 && gameInfo.goodAnswer())){
				showNoAttemptsDialog();
			}
			else if(gameInfo.goodAnswer()){
				//Загружаем следующий вопрос
				if(gameInfo.getQueIndex() < queStatusList.size()-1){
					gameInfo.setQueIndex(gameInfo.getQueIndex()+1);
					loadQuestion(queStatusList.get(gameInfo.getQueIndex()).getId());
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
	
	private void printArray(ArrayList<QueStatus> mArray){
		for(QueStatus qStatus : mArray){
			Log.d("mLog", "queId=" + qStatus.getId() + " queStatus=" + qStatus.getStatus());
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
		//Время и кол-во попыток ответа на вопрос обновляется в БД путём приращения старого значения,
		//соответственно счётсик времени и попыток нужно сбрасывать после каждого вызова метода updateGame()
		gameInfo.setQueAttemptsSpent(0);
		gameInfo.setQueTimePassed(0);
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
}
