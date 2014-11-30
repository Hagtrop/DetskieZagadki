package com.hagtrop.detskiezagadki;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class MyCursorLoader extends CursorLoader{
	public static final int GET_GAME_TABLE = 0;
	public static final int GET_QUESTION = 1;
	public static final int GET_VARIANTS = 2;
	private int queryIndex;
	
	private static final String GET_QUESTION_SQL = 
			"SELECT questions.question, questions.level, answers.answer " +
			"FROM questions " +
			"INNER JOIN answers " +
			"ON questions.answer_id=answers._id " +
			"WHERE questions._id=?";
	
	private static final String GET_VARIANTS_SQL = 
			"SELECT answers.answer FROM answers " +
			"INNER JOIN variants_matching " +
			"ON answers._id=variants_matching.variant_id " +
			"WHERE variants_matching.question_id=?";
	
	private BaseHelper baseHelper;
	private int questionId;
	
	public MyCursorLoader(Context context, BaseHelper baseHelper, int queryIndex, Bundle params){
		super(context);
		this.baseHelper = baseHelper;
		this.queryIndex = queryIndex;
		switch(queryIndex){
		case GET_GAME_TABLE:
			break;
		case GET_QUESTION:
			this.questionId = params.getInt("questionId");
			break;
		case GET_VARIANTS:
			this.questionId = params.getInt("questionId");
			break;
		default: break;
		}
	}
	
	public Cursor loadInBackground(){
		SQLiteDatabase database = baseHelper.getWritableDatabase();
		Cursor cursor = null;
		switch(queryIndex){
		case GET_GAME_TABLE:
			cursor = database.query(baseHelper.getTableName(), null, null, null, null, null, null);
			break;
		case GET_QUESTION:
			cursor = database.rawQuery(GET_QUESTION_SQL, new String[]{String.valueOf(questionId)});
			break;
		case GET_VARIANTS:
			cursor = database.rawQuery(GET_VARIANTS_SQL, new String[]{String.valueOf(questionId)});
			break;
		default: break;
		}
		
		return cursor;
	}

}
