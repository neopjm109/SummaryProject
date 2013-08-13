package com.wscompany.summary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WHtmlParser {

	static Pattern SCRIPT, STYLE, TAG, nTAG, ENTITY, WHITESPACE, TEST;
	
	private static String source, mAddr;
	private static StringBuffer content;
	
	public static String DownloadHtml(String addr){
		mAddr = addr;
		content = new StringBuffer();

		try{
			URL url = new URL(mAddr);
			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is, "KSC5601");
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
	
	public static String ChangeBr(String source){
		Pattern BR = Pattern.compile("<br>|<br/>");
		
		Matcher m;
		
		m = BR.matcher(source);
		source = m.replaceAll("\n");
		return source;
	}
	
	public static String RemoveTag(String source){
		SCRIPT = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
		STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);
		TAG = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		nTAG = Pattern.compile("<\\w+\\s+[^<]*\\s*");
		ENTITY = Pattern.compile("&[^;]+;");
		WHITESPACE = Pattern.compile("\\s\\s+");
				
		Matcher m;

		m = SCRIPT.matcher(source);
		source = m.replaceAll("");
		m = STYLE.matcher(source);
		source = m.replaceAll("");
		m = TAG.matcher(source);
		source = m.replaceAll("");
		m = nTAG.matcher(source);
		source = m.replaceAll("");
		m = ENTITY.matcher(source);
		source = m.replaceAll("");
		m = WHITESPACE.matcher(source);
		source = m.replaceAll("\n");
		
		TEST = Pattern.compile("");
		m = TEST.matcher(source);
		source = m.replaceAll("");
		
		return source;
	}
}
