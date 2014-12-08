package com.hagtrop.detskiezagadki;

import java.util.Arrays;

public class Pare{
	private String type;
	private boolean[] difficulty;
	
	Pare(String type, boolean[] difficulty) {
		this.type = type;
		this.difficulty = difficulty;
	}
	
	String getType() {return type;}
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
		Pare other = (Pare) obj;
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