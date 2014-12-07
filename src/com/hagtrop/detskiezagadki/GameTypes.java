package com.hagtrop.detskiezagadki;

import java.util.HashMap;

public final class GameTypes {
	
	private static final BiMap tables = new BiMap();
	static{
		tables.put("easy_simple_game", "simple", false, false);
		tables.put("medium_simple_game", "simple", true, false);
		tables.put("hard_simple_game", "simple", true, true);
		
		tables.put("easy_test_game", "test", false, false);
		tables.put("medium_test_game", "test", true, false);
		tables.put("hard_test_game", "test", true, true);
	}
	
	String getTableName(String type, boolean attemptsLimit, boolean timeLimit){
		return tables.getTableName(type, attemptsLimit, timeLimit);
	}
	
	Boolean[] getDifficultyParams(String table){
		return tables.getGameParams(table).getSecond();
	}
	
	private static class BiMap{
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
	}
}