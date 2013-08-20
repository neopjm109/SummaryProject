package com.wscompany.summary;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		Handler h = new Handler(){
			public void handleMessage(Message msg) {
				finish();
			}			
		};
		
		h.sendEmptyMessageDelayed(0, 2500);
	}

}
