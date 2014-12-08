package com.hagtrop.detskiezagadki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class BaseHelper extends SQLiteOpenHelper {

	private Context mContext;
	
	static final String BASE_NAME = "zagadkiDB";
	static final String EASY_SIMPLE_GAME = "easy_simple_game";
	static final String MEDIUM_SIMPLE_GAME = "medium_simple_game";
	static final String HARD_SIMPLE_GAME = "hard_simple_game";
	static final String EASY_TEST_GAME = "easy_test_game";
	static final String MEDIUM_TEST_GAME = "medium_test_game";
	static final String HARD_TEST_GAME = "hard_test_game";
	
	private static File BASE_FILE;
	private static BaseHelper bhInstance;
	private boolean testGameExists = false;
	private String tableName;
	
	synchronized static public BaseHelper getInstance(Context context){
		Log.d("mLog", "BaseHelper getInstance");
		if(bhInstance == null) bhInstance = new BaseHelper(context.getApplicationContext());
		return bhInstance;
	}
	
	private BaseHelper(Context context) {
		super(context, BASE_NAME, null, 1);
		Log.d("mLog", "private BaseHelper");
		mContext = context;
		SQLiteDatabase database = null;
		try{
			database = getReadableDatabase();
			if(database != null) database.close();
			BASE_FILE = context.getDatabasePath(BASE_NAME);
			copyBaseFromAssets();
		}
		catch (SQLiteException e){Log.d("mLog", e.toString());}
		finally{
			if(database != null && database.isOpen()) database.close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public void deleteTestGame(){
		testGameExists = false;
	}
	
	public void newTestGame(){
		String deleteQuery, createQuery;
		deleteQuery = "DROP TABLE IF EXISTS test_game";
		createQuery = "CREATE TABLE test_game(question_id INTEGER, status INTEGER DEFAULT 0, attempts INTEGER DEFAULT 0, time INTEGER DEFAULT 0)";
		SQLiteDatabase database = getWritableDatabase();
		database.execSQL(deleteQuery);
		database.execSQL(createQuery);
		
		//Загружаем данные из таблицы вопросов, сортируем вопросы и наполняем отсортированным списком таблицу simple_game
		Cursor cursor = database.query("questions", new String[]{"questions._id", "questions.level", "questions.answer_id"}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			ArrayList<QueParams> quesParams = new ArrayList<QueParams>();
			int queId, queLevel, answerId;
			do{
				queId = cursor.getInt(cursor.getColumnIndex("_id"));
		        queLevel = cursor.getInt(cursor.getColumnIndex("level"));
		        answerId = cursor.getInt(cursor.getColumnIndex("answer_id"));
		        quesParams.add(new QueParams(queId, queLevel, answerId));
			} while(cursor.moveToNext());
			Collections.sort(quesParams);
			ContentValues cv;
			database.beginTransaction();
			try{
				for(QueParams params : quesParams){
					cv = new ContentValues();
					cv.put("question_id", params.queId);
					database.insert("test_game", null, cv);
				}
				database.setTransactionSuccessful();
			}
			finally{
				database.endTransaction();
			}
			
		}
		database.close();
		testGameExists = true;
	}
	
	public boolean testGameExists(){
		return testGameExists;
	}
	
	public void printData(){
		Log.d("mLog", "BaseName=" + getDatabaseName());
		Log.d("mLog", "BaseFile=" + mContext.getDatabasePath(getDatabaseName()));
	}
	
	private void copyBaseFromAssets(){
		Log.d("mLog", "---------------------------");
		Log.d("mLog", "copyBaseFromAssets()");
		Log.d("mLog", "---------------------------");
		AssetManager assetManager = mContext.getResources().getAssets();
		InputStream input = null;
	    OutputStream output = null;
	    try{
	    	input = assetManager.open(BASE_NAME);
	    	output = new FileOutputStream(BASE_FILE);
	    	byte[] buffer = new byte[1024];
	        int read = 0;
	        while ((read = input.read(buffer)) != -1) {
	            output.write(buffer, 0, read);
	        }
	        Log.d("mLog", "copyBaseFromAssets: try read");
	    } catch(IOException e){Log.d("mLog", "copyBaseFromAssets: Read error " + e.toString());}
	    finally{
	    	if(input != null)
	    		try{
	    			input.close();
	    		} catch (IOException e){Log.d("mLog", e.toString());}
	    	if(output != null)
	    		try{
	    			output.close();
	    		} catch (IOException e){Log.d("mLog", e.toString());}
	    }
	}
	
	void updateTestGame(int queId, int attempts, int status){
		SQLiteDatabase database = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("attempts", attempts);
		cv.put("status", status);
		database.update("test_game", cv, "question_id=?", new String[]{String.valueOf(queId)});
	}
	
	//-------------------------------------------------------------------------//
	
	String getTableName(){
		return tableName;
	}
	
	void updateGame(int queId, int status, int attempts, long time){
		SQLiteDatabase database = getWritableDatabase();
		database.execSQL("UPDATE " + tableName + " SET status=?, attempts=attempts+?, time=time+? WHERE question_id=?", new String[]{
				String.valueOf(status), 
				String.valueOf(attempts),
				String.valueOf(time), 
				String.valueOf(queId)});
	}
	
	static public String getTableNameByGameType(int gameType, boolean useAttempts, boolean useTimer){
		String name = null;
		switch(gameType){
		case GameTypes.SIMPLE:
			if(!useAttempts && !useTimer) name = EASY_SIMPLE_GAME;
			else if(useAttempts && !useTimer) name = MEDIUM_SIMPLE_GAME;
			else if(useAttempts && useTimer) name = HARD_SIMPLE_GAME;
			break;
		case GameTypes.TEST:
			if(!useAttempts && !useTimer) name = EASY_TEST_GAME;
			else if(useAttempts && !useTimer) name = MEDIUM_TEST_GAME;
			else if(useAttempts && useTimer) name = HARD_TEST_GAME;
			break;
		default: break;
		}
		return name;
	}
	
	void newTable(String tableName){
		Log.d("mLog", "->->-BaseHelper.newGame()->->-");
		this.tableName = tableName;
		String createQuery = "CREATE TABLE " + tableName + "(question_id INTEGER, status INTEGER DEFAULT 0, attempts INTEGER DEFAULT 0, time INTEGER DEFAULT 0)";
		SQLiteDatabase database = getWritableDatabase();
		deleteOldGames(database);
		database.execSQL(createQuery);
		
		//Загружаем данные из таблицы вопросов, сортируем вопросы и наполняем отсортированным списком таблицу simple_game
		Cursor cursor = database.query("questions", new String[]{"questions._id", "questions.level", "questions.answer_id"}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			ArrayList<QueParams> quesParams = new ArrayList<QueParams>();
			int queId, queLevel, answerId;
			do{
				queId = cursor.getInt(cursor.getColumnIndex("_id"));
		        queLevel = cursor.getInt(cursor.getColumnIndex("level"));
		        answerId = cursor.getInt(cursor.getColumnIndex("answer_id"));
		        quesParams.add(new QueParams(queId, queLevel, answerId));
			} while(cursor.moveToNext());
			Collections.sort(quesParams);
			ContentValues cv;
			database.beginTransaction();
			try{
				for(QueParams params : quesParams){
					cv = new ContentValues();
					cv.put("question_id", params.queId);
					database.insert(tableName, null, cv);
				}
				database.setTransactionSuccessful();
			}
			finally{
				database.endTransaction();
			}
			
		}
		database.close();
		Log.d("mLog", "-<-<-BaseHelper.newGame()-<-<-");
	}
	
	void deleteOldGames(SQLiteDatabase database){
		Log.d("mLog", "->->-BaseHelper.deleteOldGames()->->-");
		//Удаляем все таблицы незавершённых игр, если такие существуют
		database.execSQL("DROP TABLE IF EXISTS " + EASY_SIMPLE_GAME);
		database.execSQL("DROP TABLE IF EXISTS " + MEDIUM_SIMPLE_GAME);
		database.execSQL("DROP TABLE IF EXISTS " + HARD_SIMPLE_GAME);
		
		database.execSQL("DROP TABLE IF EXISTS " + EASY_TEST_GAME);
		database.execSQL("DROP TABLE IF EXISTS " + MEDIUM_TEST_GAME);
		database.execSQL("DROP TABLE IF EXISTS " + HARD_TEST_GAME);
		Log.d("mLog", "-<-<-BaseHelper.deleteOldGames()-<-<-");
	}
	
	Bundle getSaved(){
		Log.d("mLog", "->->-BaseHelper.getSaved->->-");
		Bundle bundle = null;
		SQLiteDatabase database = getWritableDatabase();
		Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name IN (?, ?, ?, ?, ?, ?)", 
				new String[]{EASY_SIMPLE_GAME, MEDIUM_SIMPLE_GAME, HARD_SIMPLE_GAME, EASY_TEST_GAME, MEDIUM_TEST_GAME, HARD_TEST_GAME});
		if(cursor.moveToFirst()){
			bundle = new Bundle();
			bundle.putString("table", cursor.getString(0));
		}
		Log.d("mLog", "-<-<-BaseHelper.getSaved-<-<-");
		return bundle;
	}

}
