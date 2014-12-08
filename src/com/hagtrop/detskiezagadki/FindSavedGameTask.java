package com.hagtrop.detskiezagadki;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class FindSavedGameTask extends AsyncTask<Void, Void, Bundle> {
	
	private OnAsyncTaskComplete onTaskComplete;
	private BaseHelper baseHelper;
	
	public FindSavedGameTask(BaseHelper baseHelper, OnAsyncTaskComplete onTaskComplete){
		this.baseHelper = baseHelper;
		this.onTaskComplete = onTaskComplete;
	}

	@Override
	protected Bundle doInBackground(Void... params) {
		Log.d("mLog", "In FindSavedGameTask.doInBackground");
		return baseHelper.getSaved();
	}

	@Override
	protected void onPostExecute(Bundle result) {
		onTaskComplete.onComplete(result);
	}
}
