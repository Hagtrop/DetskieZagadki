package com.hagtrop.detskiezagadki;

import java.util.Arrays;
import java.util.HashMap;

public final class GameTypes {
	private static HashMap<String, Params> straight;
	private static HashMap<Params, String> inversed;
	
	public static final int SIMPLE = 1;
	public static final int TEST = 2;
	
	static{
		straight = new HashMap<String, Params>();
		inversed = new HashMap<Params, String>();
		
		put(BaseHelper.EASY_SIMPLE_GAME, SIMPLE, false, false);
		put(BaseHelper.MEDIUM_SIMPLE_GAME, SIMPLE, true, false);
		put(BaseHelper.HARD_SIMPLE_GAME, SIMPLE, true, true);
		
		put(BaseHelper.EASY_TEST_GAME, TEST, false, false);
		put(BaseHelper.MEDIUM_TEST_GAME, TEST, true, false);
		put(BaseHelper.HARD_TEST_GAME, TEST, true, true);
	}
	
	private static void put(String table, Integer type, boolean attemptsLimit, boolean timeLimit){
		Params params = new Params(type, new boolean[]{attemptsLimit, timeLimit});
		straight.put(table, params);
		inversed.put(params, table);
	}
	
	static String getTableName(Integer type, boolean attemptsLimit, boolean timeLimit){
		Params params = new Params(type, new boolean[]{attemptsLimit, timeLimit});
		return inversed.get(params);
	}
	
	static Integer getGameType(String table){
		return straight.get(table).getType();
	}
	
	static boolean[] getDifficultyParams(String table){
		return straight.get(table).getDifficulty();
	}
	
	private static class Params{
		private Integer type;
		private boolean[] difficulty;
		
		Params(Integer type, boolean[] difficulty) {
			this.type = type;
			this.difficulty = difficulty;
		}
		
		Integer getType() {return type;}
		boolean[] getDifficulty() {return difficulty;}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(difficulty);
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Params other = (Params) obj;
			if (!Arrays.equals(difficulty, other.difficulty))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
}