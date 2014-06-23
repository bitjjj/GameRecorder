package com.gamerecorder.task;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.gamerecorder.activity.R;
import com.gamerecorder.util.FileUtil;

public class LoadVSHistoryDataAsyncTask extends AsyncTask<Void, Void, List<Object>>{

	public interface LoadVSHistoryDataCallback{
		public void success(Object result);
	}
	
	private Context ctx = null;
	private LoadVSHistoryDataCallback callback;
	private boolean isShowLoading = false;
	private ProgressDialog proDialog;
	
	public LoadVSHistoryDataAsyncTask(Context ctx,LoadVSHistoryDataCallback callback){
		this(ctx,callback,false);
	}
	
	public LoadVSHistoryDataAsyncTask(Context ctx,LoadVSHistoryDataCallback callback,boolean isShowLoading){
		this.ctx = ctx;
		this.callback = callback;
		this.isShowLoading = false;
	}
	
	@Override
	protected void onPreExecute() {
		if(isShowLoading){
			proDialog = ProgressDialog.show(this.ctx, 
					this.ctx.getResources().getString(R.string.vs_history_loading_title), this.ctx.getResources().getString(R.string.vs_history_loading_message));
		}
	}

	
	@Override
	protected List<Object> doInBackground(Void... params) {
		return FileUtil.getGameHistoryFile(this.ctx);
	}
	
	@Override
	protected void onPostExecute(List<Object> history) {
          
		callback.success(history);
		
		if(isShowLoading){
			proDialog.dismiss();
		}
	}
	
}
