package com.hagtrop.detskiezagadki;

import android.util.Log;

public class GameInfo {
	final boolean USE_TIMER, USE_ATTEMPTS_LIMIT;
	private long timeLimit, timePassed, queTimePassed, timeLeft;
	private int queIndex, queLevel;
	private int attemptsLimit, attemptsSpent, queAttemptsSpent;
	private boolean goodAnswer;
	private String question, answer;
	private boolean gameOver;
	
	GameInfo(boolean useTimer, boolean useAttemptsLimit){
		USE_TIMER = useTimer;
		USE_ATTEMPTS_LIMIT = useAttemptsLimit;
		timeLimit = 0;
		timePassed = 0;
		queTimePassed = 0;
		timeLeft = 0;
		queIndex = -1;
		attemptsLimit = 0;
		attemptsSpent = 0;
		queAttemptsSpent = 0;
		goodAnswer = false;
		gameOver = false;
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
		setQueTimePassed(queTimePassed + millis);
		setTimePassed(timePassed + millis);
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
		setQueAttemptsSpent(0);
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
	
	void setAttemptsLimit(int limit){
		attemptsLimit = limit;
	}
	
	int getAttemptsLimit(){
		return attemptsLimit;
	}
	
	void setAttemptsSpent(int count){
		attemptsSpent = count;
	}
	
	int getAttemptsSpent(){
		return attemptsSpent;
	}
	
	void setQueAttemptsSpent(int count){
		queAttemptsSpent = count;
	}
	
	int getQueAttemptsSpent(){
		return queAttemptsSpent;
	}
	
	void addAttemptsSpent(int count){
		setAttemptsSpent(attemptsSpent + count);
	}
	
	void addQueAttemptsSpent(int count){
		setQueAttemptsSpent(queAttemptsSpent + count);
		addAttemptsSpent(count);
	}
	
	int getAttemptsRemaining(){
		return attemptsLimit - attemptsSpent;
	}
	
	void setGameOver(boolean isOver){
		gameOver = isOver;
	}
	
	boolean gameOver(){
		return gameOver;
	}
}
