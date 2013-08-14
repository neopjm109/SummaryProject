package com.wscompany.summary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WHtmlParser {

	static Pattern BR, TITLE, A, B, H, STRONG, SPAN, BUTTON;
	static Pattern SCRIPT, STYLE, TAG, nTAG, ENTITY, WHITESPACE;
	
	private static String source, mAddr;
	private static StringBuffer content;
	
	public static String DownloadHtml(String addr){
		mAddr = addr;
		content = new StringBuffer();

		try{
			URL url = new URL(mAddr);
			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
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
	
	public static String GetTitle(String source){
		String title = source;
		boolean titleOn = false;
		int titleStart = 0, titleEnd = 0;
		
		Matcher m;

		TITLE = Pattern.compile("<title[^>]*>|</title>");
		m = TITLE.matcher(source);
		title = m.replaceAll("£À");
		
		for(int i=0; i<title.length(); i++){
			if(!titleOn && title.charAt(i) == '£À'){
				titleOn = true;
				titleStart = i;
			}else if(titleOn && title.charAt(i) == '£À'){
				titleEnd = i;
				break;
			}
		}
		
		title = title.substring(titleStart+1, titleEnd);
		
		return title;
	}
		
	public static String RemoveTag(String source){
		BR = Pattern.compile("<br>|<br/>");
		TITLE = Pattern.compile("<title[^>]*>.*</title>");
		A = Pattern.compile("<a[^>]*>.*?</a>");
		B = Pattern.compile("<b>.*?</b>", Pattern.DOTALL);
		H = Pattern.compile("<h[^>]*>.*?</h[^>]>", Pattern.DOTALL);
		SPAN = Pattern.compile("<span[^>]*>.*?</span>", Pattern.DOTALL);
		STRONG = Pattern.compile("<strong[^>]*>.*?</strong>");
		BUTTON = Pattern.compile("<button[^>]*>.*?</button>");
		
		SCRIPT = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
		STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);
		TAG = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		nTAG = Pattern.compile("<\\w+\\s+[^<]*\\s*");
		ENTITY = Pattern.compile("&[^;]+;");
		WHITESPACE = Pattern.compile("\\s\\s+");
				
		Matcher m;

		m = BR.matcher(source);
		source = m.replaceAll("££");
		m = TITLE.matcher(source);
		source = m.replaceAll("");
		m = A.matcher(source);
		source = m.replaceAll("");
		m = B.matcher(source);
		source = m.replaceAll("");
		m = H.matcher(source);
		source = m.replaceAll("");
		m = SPAN.matcher(source);
		source = m.replaceAll("");
		m = STRONG.matcher(source);
		source = m.replaceAll("");
		m = BUTTON.matcher(source);
		source = m.replaceAll("");
		
		
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
		source = m.replaceAll(" ");
		
		source = source.replace("££", "\n");

		
		return source;
	}
	
	public static String RemoveSentence(String source){
		ArrayList<Integer> indices = new ArrayList<Integer>();
		boolean SentenceOn = false;
		
		for(int i=0; i<source.length(); i++){
			if(!SentenceOn && source.charAt(i) == '#'){
				if(i == source.length()-1)
					break;
				indices.add(i);
				SentenceOn = true;
			}
			if(SentenceOn && source.charAt(i) == '\n'){
				indices.add(i);
				SentenceOn = false;
			}
		}
		
		
		return source;
	}
}
