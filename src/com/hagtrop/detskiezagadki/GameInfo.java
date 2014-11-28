package com.hagtrop.detskiezagadki;

import android.util.Log;

public class GameInfo {
	final boolean USE_TIMER, USE_ATTEMPTS_LIMIT;
	private long timeLimit, timePassed, queTimePassed, timeLeft;
	private int queIndex, queLevel;
	private boolean goodAnswer;
	private String question, answer;
	
	GameInfo(boolean useTimer, boolean useAttemptsLimit){
		USE_TIMER = useTimer;
		USE_ATTEMPTS_LIMIT = useAttemptsLimit;
		timeLimit = 0;
		timePassed = 0;
		queTimePassed = 0;
		timeLeft = 0;
		queIndex = -1;
		goodAnswer = false;
	}
	
	void setTimeLimit(long millis){
		timeLimit = millis;
		timeLeft = timeLimit - timePassed;
	}
	
	long getTimeLimit(){
		return timeLimit;
	}
	
	void setTimePassed(long millis){
		timePassed = millis;
		timeLeft = timeLimit-timePassed;
	}
	
	void addQueTimePassed(long millis){
		setQueTimePassed(getQueTimePassed() + millis);
		setTimePassed(getTimePassed() + millis);
	}
	
	long getTimePassed(){
		return timePassed;
	}
	
	void setQueTimePassed(long millis){
		queTimePassed = millis;
		Log.d("mLog", "setQueTimePassed = " + queTimePassed);
	}
	
	long getQueTimePassed(){
		return queTimePassed;
	}
	
	long getTimeLeft(){
		return timeLeft;
	}
	
	void setQueIndex(int index){
		queIndex = index;
		Log.d("mLog", "setQueIndex(" + index + ")");
	}
	
	int getQueIndex(){
		return queIndex;
	}
	
	void newQue(String question, String answer, int level){
		setQuestion(question);
		setAnswer(answer);
		setQueLevel(level);
		setQueTimePassed(0);
		goodAnswer(false);
	}
	
	void goodAnswer(boolean result){
		goodAnswer = result;
	}
	
	boolean goodAnswer(){
		return goodAnswer;
	}
	
	void setQuestion(String question){
		this.question = question;
	}
	
	String getQuestion(){
		return question;
	}
	
	void setAnswer(String answer){
		this.answer = answer;
	}
	
	String getAnswer(){
		return answer;
	}
	
	void setQueLevel(int level){
		this.queLevel = level;
	}
	
	int getQueLevel(){
		return queLevel;
	}
}
