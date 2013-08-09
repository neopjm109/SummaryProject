package com.example.summaryproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// UI Variables
	private Button btnSummary, btnLoad, btnTextSizeUp, btnTextSizeDown;
	private TextView mtext;
	private CharSequence[] items = {"Ž����","��������"};
	private String filePath;
	
	// Variables
	private String beforeSummary, afterSummary = "����ϱ�!";
	private boolean btnSummaryOn = false;
	private int textSizeCount = 0;

	// Thread
	DownThread mThread;
	private ProgressDialog mProgress;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ���� ������
		filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator;

		// MainLayer UI
		btnSummary = (Button)findViewById(R.id.btnSummary);
		btnLoad = (Button)findViewById(R.id.btnLoad);
		mtext = (TextView)findViewById(R.id.mtext);
		btnSummary.setOnClickListener(listener);
		btnLoad.setOnClickListener(listener);

		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout upLayer = (LinearLayout)inflater.inflate(R.layout.up_layer, null);

		// SubLayer(UpLayer) UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		addContentView(upLayer, params);

		btnTextSizeUp = (Button)upLayer.findViewById(R.id.btnTextSizeUp);
		btnTextSizeDown = (Button)upLayer.findViewById(R.id.btnTextSizeDown);
		
		btnTextSizeUp.setOnClickListener(listener);
		btnTextSizeDown.setOnClickListener(listener);
	}
	View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			// �۾� Ȯ�� ���
			if(v.getId() == R.id.btnTextSizeUp){
				if(textSizeCount < 3){
					switch(textSizeCount){
					case 0:
						mtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
						break;
					case 1:
						mtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
						break;
					case 2:
						mtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
						break;
					default:
						Toast.makeText(getApplicationContext(), "���̻� �ø� �� �����ϴ�.", Toast.LENGTH_SHORT);
						break;
					}
					textSizeCount++;
				}
			}else if(v.getId() == R.id.btnTextSizeDown){
				if(textSizeCount > 0){	
					switch(textSizeCount){
					case 1:
						mtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
						break;
					case 2:
						mtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
						break;
					case 3:
						mtext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
						break;
					default:
						Toast.makeText(getApplicationContext(), "���̻� ���� �� �����ϴ�.", Toast.LENGTH_SHORT);
						break;
					}
					textSizeCount--;
				}
			}
			// �� �޴�
			if (v.getId() == R.id.btnSummary){
				// ����ϱ�
				if(btnSummaryOn){
					mtext.setText(beforeSummary);
					btnSummary.setText("����ϱ�");
				}
				else{
					WSummary.SummaryCheck(afterSummary);
					mtext.setText(afterSummary);
					btnSummary.setText("��������");
				}
				btnSummaryOn = !btnSummaryOn;
			}
			if(v.getId() == R.id.btnLoad){
				new AlertDialog.Builder(MainActivity.this).setTitle("�����ϼ���")
				.setItems(items,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if(items[item] == "Ž����")
							onTXTRead();
						if(items[item] == "��������"){
							LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
							LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.html_dialog, null);
							final EditText htmlEdit = (EditText)layout.findViewById(R.id.htmlEdit);
							
							new AlertDialog.Builder(MainActivity.this).setTitle("��������")
							.setView(layout).setPositiveButton("Ȯ��", new DialogInterface.OnClickListener(){
								
								public void onClick(DialogInterface dialog, int which) {
//									mProgress = ProgressDialog.show(getApplicationContext(), "�������� �ҷ�����", "�ٿ� �޴���...");
								
									mThread = new DownThread(htmlEdit.getText().toString());
									mThread.start();
								}
							}).setNegativeButton("���", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									}
							}).show();
						 }
					}
				}).show();
			}
		}
	};

	// Ž����
	public void onTXTRead(){
		final ArrayList<File> fileList = new ArrayList<File>();
		File files = new File(filePath);
		if(!files.exists()){
			files.mkdirs();
		}
		if(files.listFiles().length > 0 ){
			for(File file : files.listFiles(new TXTFilter())){
				if(file.isDirectory()){
				}
				fileList.add(file);
			}
		}
		
		CharSequence[] filename = new CharSequence[fileList.size()];
		for(int i = 0; i < fileList.size(); i++)
			filename[i] = fileList.get(i).getName();
		
		new AlertDialog.Builder(this).setTitle("TEXT FILE LIST").setItems(filename, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try{
					String body = "";
					StringBuffer bodyText = new StringBuffer();
					File selectText = fileList.get(which);
					FileInputStream fis = new FileInputStream(selectText);
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis, "KSC5601"));
					
					while((body = bufferReader.readLine()) != null)
							bodyText.append(body);

					beforeSummary = bodyText.toString();
					mtext.setText(beforeSummary);
				}catch(IOException e){
					
				}
			}
		}).setNegativeButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		}).show();
	}
	
	// Html �ҷ��ö� �ʿ��� �Ǵٸ� ������
	class DownThread extends Thread{
		String mAddr;
		String mResult;
		
		DownThread(String addr){
			mAddr = addr;
			mResult = "";
		}
		
		public void run(){
			mResult = WHtmlConverter.DownloadHtml(mAddr);
			mAfterDown.sendEmptyMessage(0);
		}		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg) {
				beforeSummary = mThread.mResult;
				beforeSummary = WHtmlConverter.OrderSentence(mAddr, mResult);
	    		mtext.setText(beforeSummary);
	    		
//	    		mProgress.dismiss();
	    	}
		};

	}
};

// ������ TXT�� �޾ƿ���
class TXTFilter implements FileFilter{
	public boolean accept(File file) {
		if(file.getName().endsWith(".txt"))
			return true;
		return false;
	}
}
