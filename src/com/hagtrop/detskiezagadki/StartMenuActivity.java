package com.hagtrop.detskiezagadki;

import android.app.Activity;
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
			setDifficultyOptionsVisible();
			break;
		case R.id.a0_variantsBtn:
			gameType = TEST_GAME;
			setDifficultyOptionsVisible();
			break;
		case R.id.a0_continueBtn:
			break;
		case R.id.a0_easyBtn:
			startNewGame(gameType, EASY_GAME);
			break;
		case R.id.a0_mediumBtn:
			startNewGame(gameType, MEDIUM_GAME);
			break;
		case R.id.a0_hardBtn:
			startNewGame(gameType, HARD_GAME);
			break;
		default: break;
		}
	}
	
	private void setDifficultyOptionsVisible(){
		//Скрываем кнопки выбора режима игры
		simpleBtn.setVisibility(View.GONE);
		variantsBtn.setVisibility(View.GONE);
		continueBtn.setVisibility(View.GONE);
		//Отображаем кнопки выбора сложности
		headerTV.setVisibility(View.VISIBLE);
		easyBtn.setVisibility(View.VISIBLE);
		mediumBtn.setVisibility(View.VISIBLE);
		hardBtn.setVisibility(View.VISIBLE);
	}
	
	private void startNewGame(int gameType, int gameDifficulty){
		
	}
}
