package com.hagtrop.detskiezagadki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartMenuActivity extends Activity implements OnClickListener{
	TextView headerTV;
	Button simpleBtn, variantsBtn, continueBtn, easyBtn, mediumBtn, hardBtn;
	
	public static final int SIMPLE_GAME = 1;
	public static final int TEST_GAME = 2;
	
	public static final int EASY_GAME = 1;
	public static final int MEDIUM_GAME = 2;
	public static final int HARD_GAME = 3;

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
	public void onClick(View v) {
		int gameType = SIMPLE_GAME;
		switch(v.getId()){
		case R.id.a0_simpleBtn:
			gameType = SIMPLE_GAME;
			setDifficultyLevelsVisible(true);
			break;
		case R.id.a0_variantsBtn:
			gameType = TEST_GAME;
			setDifficultyLevelsVisible(true);
			break;
		case R.id.a0_continueBtn:
			break;
		case R.id.a0_easyBtn:
			startNewGame(gameType, false, false);
			break;
		case R.id.a0_mediumBtn:
			startNewGame(gameType, true, false);
			break;
		case R.id.a0_hardBtn:
			startNewGame(gameType, true, true);
			break;
		default: break;
		}
	}
	
	private void setDifficultyLevelsVisible(boolean visible){
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
	
	private void startNewGame(int gameType, boolean useTimer, boolean useAttemptsLimit){
		Intent intent = new Intent();
		switch(gameType){
		case SIMPLE_GAME:
			intent.setClass(this, SimpleGame.class);
			break;
		case TEST_GAME:
			break;
		default: break;
		}
		intent.putExtra("useTimer", useTimer);
		intent.putExtra("useAttemptsLimit", useAttemptsLimit);
		
		startActivity(intent);
	}
}
