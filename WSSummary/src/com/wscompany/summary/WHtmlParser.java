package com.wscompany.summary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

public class WHtmlParser {

	Pattern script, css, html;
	
	static String source, mAddr;
	static StringBuffer content;
	
	public static String DownloadHtml(String addr){
		mAddr = addr;
		content = new StringBuffer();

		try{
			URL url = new URL(mAddr);
			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String inStr = "";
			while((inStr = br.readLine()) != null){
				content.append(inStr + "\n");
			}
			
		}catch(Exception e){
			
		}
		
		source = new String(content);
		return source;
	}
	
	public static void ChangeBr(String source){
		
	}
	
	public static void RemoveTag(String source){
		
	}
}
