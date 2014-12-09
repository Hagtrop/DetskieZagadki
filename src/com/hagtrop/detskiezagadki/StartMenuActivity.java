package com.hagtrop.detskiezagadki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartMenuActivity extends Activity implements OnClickListener{
	
	private TextView headerTV;
	private Button simpleBtn, variantsBtn, continueBtn, easyBtn, mediumBtn, hardBtn;
	private int gameType;
	private boolean useAttemptsLimit, useTimer;
	
	/*public static final int EASY_GAME = 1;
	public static final int MEDIUM_GAME = 2;
	public static final int HARD_GAME = 3;*/   	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a0_start_menu);
        
        Log.d("mLog", "---StartMenuActivity.onCreate(){...---");
        
        headerTV = (TextView) findViewById(R.id.a0_headerTV);
        
        simpleBtn = (Button) findViewById(R.id.a0_simpleBtn);
        simpleBtn.setOnClickListener(this);
        
        variantsBtn = (Button) findViewById(R.id.a0_variantsBtn);
        variantsBtn.setOnClickListener(this);
        
        continueBtn = (Button) findViewById(R.id.a0_continueBtn);
        continueBtn.setOnClickListener(this);
        
        easyBtn = (Button) findViewById(R.id.a0_easyBtn);
        easyBtn.setOnClickListener(this);
        
        mediumBtn = (Button) findViewById(R.id.a0_mediumBtn);
        mediumBtn.setOnClickListener(this);
        
        hardBtn = (Button) findViewById(R.id.a0_hardBtn);
        hardBtn.setOnClickListener(this);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		BaseHelper baseHelper = BaseHelper.getInstance(this);
		FindSavedGameTask task = new FindSavedGameTask(baseHelper, new OnAsyncTaskComplete() {
			@Override
			public void onComplete(Bundle result) {
				if(result != null){
					String table = result.getString("table");
					gameType = GameTypes.getGameType(table);
					useAttemptsLimit = GameTypes.getDifficultyParams(table)[0];
					useTimer = GameTypes.getDifficultyParams(table)[1];
					
					continueBtn.setEnabled(true);
				}
		        else continueBtn.setEnabled(false);
			}
		});
		task.execute();
        baseHelper.close();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.a0_simpleBtn:
			gameType = GameTypes.SIMPLE;
			showDifficultyBtns(true);
			return;
		case R.id.a0_variantsBtn:
			gameType = GameTypes.TEST;
			showDifficultyBtns(true);
			return;
		case R.id.a0_continueBtn:
			startGame();
			return;
		case R.id.a0_easyBtn:
			useAttemptsLimit = false;
			useTimer = false;
			break;
		case R.id.a0_mediumBtn:
			useAttemptsLimit = true;
			useTimer = false;
			break;
		case R.id.a0_hardBtn:
			useAttemptsLimit = true;
			useTimer = true;
			break;
		default: break;
		}
		createNewGame();
	}
	
	private void showDifficultyBtns(boolean visible){
		int types, levels;
		if(visible){
			types = View.GONE;
			levels = View.VISIBLE;
		}
		else{
			types = View.VISIBLE;
			levels = View.GONE;
		}
		
		//Скрываем/отображаем кнопки выбора режима игры
		simpleBtn.setVisibility(types);
		variantsBtn.setVisibility(types);
		continueBtn.setVisibility(types);
		//Отображаем/скрываем кнопки выбора сложности
		headerTV.setVisibility(levels);
		easyBtn.setVisibility(levels);
		mediumBtn.setVisibility(levels);
		hardBtn.setVisibility(levels);
	}
	
	private void createNewGame(){
		BaseHelper baseHelper = BaseHelper.getInstance(this);
		CreateNewGameTask task = new CreateNewGameTask(baseHelper, GameTypes.getTableName(gameType, useAttemptsLimit, useTimer), new OnAsyncTaskComplete() {
			
			@Override
			public void onComplete(Bundle params) {
				startGame();
			}
		});
		task.execute();
		baseHelper.close();
	}
	
	private void startGame(){
		Log.d("mLog", "START: " + System.currentTimeMillis());
		Log.d("mLog", "gameType: " + gameType);
		Log.d("mLog", "useAttemptsLimit: " + useAttemptsLimit);
		Log.d("mLog", "useTimer: " + useTimer);
		Intent intent = new Intent();
		switch(gameType){
		case GameTypes.SIMPLE:
			intent.setClass(this, SimpleGameActivity.class);
			break;
		case GameTypes.TEST:
			break;
		default: break;
		}
		intent.putExtra("useAttemptsLimit", useAttemptsLimit);
		intent.putExtra("useTimer", useTimer);
		
		startActivityForResult(intent, 1);
		Log.d("mLog", "END: " + System.currentTimeMillis());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		showDifficultyBtns(false);
	}
}
