package com.hagtrop.detskiezagadki;

public class Pare<First, Second> {
	private First first = null;
	private Second second = null;
	
	Pare(First first, Second second) {
		this.first = first;
		this.second = second;
	}
	
	First getFirst() {return first;}
	Second getSecond() {return second;}
}