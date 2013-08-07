package com.example.summaryproejct;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button btnSummary, btnLoad;
	TextView mtext;
	CharSequence[] items = {"탐색기","웹페이지"};
	private String filePath;
	
	String beforeSummary, afterSummary = "요약하기!";
	boolean btnSummaryOn = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator;
		
		btnSummary = (Button) findViewById(R.id.btnSummary);
		btnLoad = (Button)findViewById(R.id.btnLoad);
		mtext = (TextView)findViewById(R.id.mtext);
		btnSummary.setOnClickListener(listener);
		btnLoad.setOnClickListener(listener);

	}
	View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			  if (v.getId() == R.id.btnSummary){
				  // 요약하기
				  if(btnSummaryOn){
					  mtext.setText(beforeSummary);
					  btnSummary.setText("요약하기");
				  }
				  else{
					  WSummary.SummaryCheck(afterSummary);
					  mtext.setText(afterSummary);
					  btnSummary.setText("원문보기");
				  }
				  btnSummaryOn = !btnSummaryOn;
				  
			  }else if(v.getId() == R.id.btnLoad){
				  new AlertDialog.Builder(MainActivity.this).setTitle("선택하세요")
				  .setItems(items,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						// TODO Auto-generated method stub
						 if(items[item] == "탐색기")
							 onTXTRead();
						 if(items[item] == "웹페이지"){
							 LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
							 View layout = inflater.inflate(R.layout.html_dialog, (ViewGroup)findViewById(R.id.htmlLayout));
							 final EditText htmlEdit = (EditText)layout.findViewById(R.id.htmlEdit);
							 
							 new AlertDialog.Builder(MainActivity.this).setTitle("웹페이지")
							 .setView(layout).setPositiveButton("확인", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									onHtmlRead(htmlEdit.getText().toString());
								}
								 
							 }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}).show();
						 }
					}
				}).show();
			  }
		}
	};
	
	public void onHtmlRead(String addr){
		String mAddr = addr;
		String str = WHtmlConverter.DownloadHtml(mAddr);
		str = WHtmlConverter.OrderSentence(mAddr, str);
		mtext.setText(str);
	}
	

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
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
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
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
	}
	
	public void onDOCRead(){
		final ArrayList<File> fileList = new ArrayList<File>();
		File files = new File(filePath);
		if(!files.exists()){
			files.mkdirs();
		}
		if(files.listFiles().length > 0 ){
			for(File file : files.listFiles(new DOCFilter())){
				fileList.add(file);
			}
		}
		
		CharSequence[] filename = new CharSequence[fileList.size()];
		for(int i = 0; i < fileList.size(); i++)
			filename[i] = fileList.get(i).getName();
		
		new AlertDialog.Builder(this).setTitle("DOC FILE LIST").setItems(filename, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try{
					String body = "";
					StringBuffer bodyText = new StringBuffer();
					File selectText = fileList.get(which);
					FileInputStream fis = new FileInputStream(selectText);
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					byte[] bytes = new byte[4096];
					int length = 0;
					byteStream.write(bytes, 0, length);
					
					while((body = bufferReader.readLine()) != null)
							bodyText.append(body);

					beforeSummary = bodyText.toString();
					mtext.setText(beforeSummary);
				}catch(IOException e){
					
				}
			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
	}
};

class TXTFilter implements FileFilter{
	
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		if(file.getName().endsWith(".txt"))
			return true;
		return false;
	}
}

class DOCFilter implements FileFilter{
	
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		if(file.getName().endsWith(".doc") || file.getName().endsWith(".docx"))
			return true;
		return false;
	}
}