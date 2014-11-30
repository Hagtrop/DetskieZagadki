package com.hagtrop.detskiezagadki;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//Класс для хранения кнопок строки ответа и соверщения операций над ними
class AnswerButtonsArray{
	private ArrayList<Button> buttons; //кнопки строки ответа, включая скрытые
	private Button[] letters; //ссылки на нажатые буквы
	
	public AnswerButtonsArray(){
		buttons = new ArrayList<Button>();
	}
	
	void add(Button button){
		buttons.add(button);
	}
	
	void setLetter(int position, Button letter){
		//Если позиция в строке ответа не пуста, то сначала очищаем её, а потом заносим новую букву
		if(letters[position] != null) deleteLetter(position);
		letters[position] = letter;
		buttons.get(position).setText(letter.getText());
	}
	
	private void deleteLetter(int position){
		//По позиции получаем из letters ссылку на скрытую кнопку, делаем её видимой
		if(!isEmpty(position)){
			getPressedBtn(position).setVisibility(View.VISIBLE);
			buttons.get(position).setText(null);
			letters[position] = null;
		}
	}
	
	void deleteLetter(Button btn){
		//Получаем позицию нажатой кнопки в строке ответа
		int position = indexOf(btn);
		deleteLetter(position);
	}
	
	int size(){
		return buttons.size();
	}
	
	//Отображаем количество кнопок в строке ответа соответствующее количеству букв в слове
	void setVisible(int count){
		for(int i=0; i<count; i++) buttons.get(i).setVisibility(View.VISIBLE);
		letters = new Button[count];
		for(int i=count; i<buttons.size(); i++) buttons.get(i).setVisibility(View.GONE);
	}
	
	//Удаляем буквы с кнопок строки ответа
	void clearText(){
		for(Button button : buttons){
			button.setText(null);
		}
	}
	
	//Возвращаем позицию в строке ответа, на которую встала нажатая буква
	int getLetterPosition(Button button){
		Log.d("mLog", "button id: " + button.getId());
		Log.d("mLog", "----------------");
		for(int i=0; i<letters.length; i++){
			if(letters[i] != null) Log.d("mLog", i+": "+letters[i].getId());
		}
		Log.d("mLog", "----------------");
		return Arrays.asList(letters).indexOf(button);
	}
	
	//Возвращаем ссылку на кнопку из набора букв, соответствующую нажатой букве в строке ответа
	Button getPressedBtn(int position){
		if(letters[position] != null) return letters[position];
		else return null;
	}
	
	//Возвращаем порядковый номер кнопки в строке ответа
	int indexOf(Button button){
		return buttons.indexOf(button);
	}
	
	//Назначаем общий обработчик всем кнопкам строки ответа
	void setOnClickListener(OnClickListener listener){
		for(Button button : buttons) button.setOnClickListener(listener);
	}
	
	//Проверяем позицию в строке ответа по наличию ссылки в массиве letters
	boolean isEmpty(int position){
		if(letters[position] == null) return true;
		else return false;
	}
	
	int getFirstEmptyBtn(){
		int position = -1;
		for(int i=0; i<letters.length; i++){
			if(letters[i] == null){
				position = i;
				break;
			}
		}
		return position;
	}
	
	String getPlayerAnswer(){
		String result = "";
		for(Button btn : buttons){
			if(btn.getVisibility() == View.VISIBLE) result += btn.getText();
		}
		return result;
	}
	
	/*void setFocusedBg(int position){
		buttons.get(position).setBackground(android.R.drawable.btn_default_small_pressed);
	}*/
}