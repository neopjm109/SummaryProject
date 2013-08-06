package com.example.summaryproejct;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WHtmlConverter {

	public static String DownloadHtml(String addr){
		String source = "";
		StringBuffer content = new StringBuffer();
		
		try{
			URL url = new URL(addr);
			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String inStr = "";
			while((inStr = br.readLine()) != null){
				content.append(inStr + "\n");
			}
			
			source = new String(content);
			
		}catch(Exception e){
			
		}
		
		return source;
	}
}
