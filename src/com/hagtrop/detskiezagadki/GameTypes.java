package com.hagtrop.detskiezagadki;

import java.util.HashMap;

import android.util.Log;

public final class GameTypes {
	private static HashMap<String, Pare> straight;
	private static HashMap<Pare, String> inversed;
	
	//private static final BiMap tables = new BiMap();
	static{
		straight = new HashMap<String, Pare>();
		inversed = new HashMap<Pare, String>();
		put("easy_simple_game", "simple", false, false);
		put("medium_simple_game", "simple", true, false);
		put("hard_simple_game", "simple", true, true);
		
		put("easy_test_game", "test", false, false);
		put("medium_test_game", "test", true, false);
		put("hard_test_game", "test", true, true);
	}
	
	private static void put(String table, String type, boolean attemptsLimit, boolean timeLimit){
		Pare params = new Pare(type, new boolean[]{attemptsLimit, timeLimit});
		straight.put(table, params);
		inversed.put(params, table);
	}
	
	static String getTableName(String type, boolean attemptsLimit, boolean timeLimit){
		Pare params = new Pare(type, new boolean[]{attemptsLimit, timeLimit});
		return inversed.get(params);
	}
	
	static String getGameType(String table){
		return straight.get(table).getType();
	}
	
	static boolean[] getDifficultyParams(String table){
		return straight.get(table).getDifficulty();
	}
	
	static void logTypes(){
		Log.d("mLog", "--->--LogTypes--->--");
		Log.d("mLog", "simple, true, false: " + getTableName("simple", true, false));
		Log.d("mLog", "medium_simple_game: " + getGameType("medium_simple_game"));
		Log.d("mLog", "medium_simple_game: " + getDifficultyParams("medium_simple_game")[0] + ", " + getDifficultyParams("medium_simple_game")[1]);
		Log.d("mLog", "---<--LogTypes--<---");
	}
	
	/*private static class BiMap{
		private HashMap<String, Pare<String, Boolean[]>> straight;
		private HashMap<Pare<String, Boolean[]>, String> inversed;
		
		BiMap(){
			straight = new HashMap<String, Pare<String, Boolean[]>>();
			inversed = new HashMap<Pare<String, Boolean[]>, String>();
		}
		
		void put(String table, String type, boolean attemptsLimit, boolean timeLimit){
			Pare<String, Boolean[]> params = new Pare<String, Boolean[]>(type, new Boolean[]{attemptsLimit, timeLimit});
			straight.put(table, params);
			inversed.put(params, table);
		}
		
		Pare<String, Boolean[]> getGameParams(String table){
			return straight.get(table);
		}
		
		String getTableName(String type, boolean attemptsLimit, boolean timeLimit){
			return inversed.get(new Pare<String, Boolean[]>(type, new Boolean[]{attemptsLimit, timeLimit}));
		}
		
		HashMap<String, Pare<String, Boolean[]>> getStraight(){
			return straight;
		}
		
		HashMap<Pare<String, Boolean[]>, String> getInversed(){
			return inversed;
		}
	}*/
}