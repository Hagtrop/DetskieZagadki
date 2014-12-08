package com.hagtrop.detskiezagadki;

import android.os.AsyncTask;

public class CreateNewGameTask extends AsyncTask<Void, Void, Void> {
	
	private OnAsyncTaskComplete onTaskComplete;
	private BaseHelper baseHelper;
	private String table;
	
	public CreateNewGameTask(BaseHelper baseHelper, String table, OnAsyncTaskComplete onTaskComplete){
		this.baseHelper = baseHelper;
		this.table = table;
		this.onTaskComplete = onTaskComplete;
	}

	@Override
	protected Void doInBackground(Void... params) {
		baseHelper.newTable(table);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		onTaskComplete.onComplete(null);
	}

}
