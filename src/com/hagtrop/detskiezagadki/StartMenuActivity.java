package com.hagtrop.detskiezagadki;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartMenuActivity extends Activity implements OnClickListener{
	Bundle savedGameInfo;
	
	private TextView headerTV;
	private Button simpleBtn, variantsBtn, continueBtn, easyBtn, mediumBtn, hardBtn;
	private int gameType = SIMPLE_GAME;
	
	public static final int SIMPLE_GAME = 1;
	public static final int TEST_GAME = 2;
	
	/*public static final int EASY_GAME = 1;
	public static final int MEDIUM_GAME = 2;
	public static final int HARD_GAME = 3;*/   	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a0_start_menu);
        
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
        savedGameInfo = baseHelper.getSaved();
        if(savedGameInfo != null) continueBtn.setEnabled(true);
        else continueBtn.setEnabled(false);
        baseHelper.close();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.a0_simpleBtn:
			gameType = SIMPLE_GAME;
			showDifficultyBtns(true);
			break;
		case R.id.a0_variantsBtn:
			gameType = TEST_GAME;
			showDifficultyBtns(true);
			break;
		case R.id.a0_continueBtn:
			startGame(savedGameInfo.getInt("gameType"), savedGameInfo.getBoolean("useAttemptsLimit"), savedGameInfo.getBoolean("useTimer"), false);
			break;
		case R.id.a0_easyBtn:
			startGame(gameType, false, false, true);
			break;
		case R.id.a0_mediumBtn:
			startGame(gameType, true, false, true);
			break;
		case R.id.a0_hardBtn:
			startGame(gameType, true, true, true);
			break;
		default: break;
		}
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
	
	private void startGame(int type, boolean useAttemptsLimit, boolean useTimer, boolean newGame){
		Log.d("mLog", "gameType: " + type);
		Log.d("mLog", "useAttemptsLimit: " + useAttemptsLimit);
		Log.d("mLog", "useTimer: " + useTimer);
		Intent intent = new Intent();
		switch(type){
		case SIMPLE_GAME:
			intent.setClass(this, SimpleGame.class);
			break;
		case TEST_GAME:
			break;
		default: break;
		}
		intent.putExtra("createNewGame", newGame);
		intent.putExtra("useAttemptsLimit", useAttemptsLimit);
		intent.putExtra("useTimer", useTimer);
		
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		showDifficultyBtns(false);
	}
	
	
}
