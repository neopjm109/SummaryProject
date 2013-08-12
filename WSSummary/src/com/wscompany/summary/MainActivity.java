package com.wscompany.summary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	//
	private String beforeSummary = "", afterSummary ="";
	DownThread mThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	class DownThread extends Thread{
		String mAddr;
		String mResult;
		
		DownThread(String addr){
			mAddr = addr;
			mResult = "";
		}
		
		public void run(){
			mResult = WHtmlParser.DownloadHtml(mAddr);
			mAfterDown.sendEmptyMessage(0);
		}		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg) {
				
				beforeSummary = mThread.mResult;
				/*
				beforeSummary = WHtmlConverter.OrderSentence(mAddr, mResult);
	    		mtext.setText(beforeSummary);
	    		*/
//		    		mProgress.dismiss();
	    	}
		};
	}
}
