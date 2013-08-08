package com.example.summaryproject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class WHtmlConverter {
	
	// 네이년
	private static final int NAVER = 1000;
	private static final int NAVER_NEWS_POLICY = 1010;
	private static final int NAVER_NEWS_ECONOMY = 1011;
	private static final int NAVER_NEWS_SOCIETY = 1012;
	private static final int NAVER_NEWS_LIFE = 1013;
	private static final int NAVER_NEWS_WORLD = 1014;
	private static final int NAVER_NEWS_IT = 1015;
	private static final int NAVER_NEWS_ENTERTAINMENT = 1016;
	private static final int NAVER_NEWS_SPORTS = 1017;
	private static final int NAVER_NEWS_ISSUE = 1019;
	
	// 다옴
	private static final int DAUM = 2000;
	private static final int DAUM_NEWS_POLICY = 2010;
	private static final int DAUM_NEWS_ECONOMY = 2011;
	private static final int DAUM_NEWS_SOCIETY = 2012;
	private static final int DAUM_NEWS_CULTURE = 2013;
	private static final int DAUM_NEWS_WORLD = 2014;
	private static final int DAUM_NEWS_IT = 2015;
	private static final int DAUM_NEWS_ENTERTAINMENT = 2016;
	private static final int DAUM_NEWS_SPORTS = 2017;
	private static final int DAUM_NEWS_REALTIME = 2018;
	private static final int DAUM_NEWS_ISSUE = 2019;

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
	
	public static String EraseTag(String str){
		String result = str, source = str;
		boolean tagOn = false;
		ArrayList<Integer> tagIndices = new ArrayList<Integer>();

		// 개행 처리
		result = result.replaceAll("<br />", "\n");
		result = result.replaceAll("<br>", "\n");
		
		// tag 체크
		for(int i=0; i<source.length(); i++){
			if(!tagOn && source.charAt(i) == '<'){
				tagIndices.add(i);
				tagOn = true;
			}
			if(tagOn && source.charAt(i) == '>'){
				tagIndices.add(i);
				tagOn = false;
			}
		}

		// tag 전부 지움
		for(int i=0; i<tagIndices.size(); i+=2)
			result = result.replace(str.substring(tagIndices.get(i), tagIndices.get(i+1)+1), "");

		// 태그 나머지 처리
		result = result.replaceAll("&lt;", "<");
		result = result.replaceAll("&gt;", ">");
		result = result.replaceAll("&quot;", "\"");
		result = result.replaceAll("&nbsp;", " ");
		
		return result;
	}
	
	public static String OrderSentence(String addr, String str){
		// 네이버인지 다음인지 구분
		int num=0;
		String naver="naver", daum="daum";
		String[] category ={"sports","entertain","realtime","society","politics","economic","world","it","culture","issue"};
		String[] sid = {"sid1=100","sid1=101","sid1=102","sid1=103","sid1=104","sid1=105","sid1=106","sid1=&"};
		
		for(int i=0; i<addr.length(); i++){
			if(naver.equals(addr.substring(i, i+naver.length()))){
				num=NAVER;
				break;
			}
			if(daum.equals(addr.substring(i, i+daum.length()))){
				num=DAUM;
				break;
			}
		}
		
		switch(num){
		case NAVER:
			for(int i=0; i<addr.length(); i++){
				if(category[0].equals(addr.substring(i, i+category[0].length()))){
					num=NAVER_NEWS_SPORTS;
					break;
				}
				if(sid[0].equals(addr.substring(i, i+sid[0].length()))){
					num=NAVER_NEWS_POLICY;
					break;
				}
				if(sid[1].equals(addr.substring(i, i+sid[1].length()))){
					num=NAVER_NEWS_ECONOMY;
					break;
				}
				if(sid[2].equals(addr.substring(i, i+sid[2].length()))){
					num=NAVER_NEWS_SOCIETY;
					break;
				}
				if(sid[3].equals(addr.substring(i, i+sid[3].length()))){
					num=NAVER_NEWS_LIFE;
					break;
				}
				if(sid[4].equals(addr.substring(i, i+sid[4].length()))){
					num=NAVER_NEWS_WORLD;
					break;
				}
				if(sid[5].equals(addr.substring(i, i+sid[5].length()))){
					num=NAVER_NEWS_IT;
					break;
				}
				if(sid[6].equals(addr.substring(i, i+sid[6].length()))){
					num=NAVER_NEWS_ENTERTAINMENT;
					break;
				}
				if(sid[7].equals(addr.substring(i, i+sid[7].length()))){
					num=NAVER_NEWS_ISSUE;
					break;
				}
			}
			break;
		case DAUM:
			for(int i=0; i<addr.length(); i++){
				if(category[0].equals(addr.substring(i, i+category[0].length()))){
					num=DAUM_NEWS_SPORTS;
					break;
				}
				if(category[1].equals(addr.substring(i, i+category[1].length()))){
					num=DAUM_NEWS_ENTERTAINMENT;
					break;
				}
				if(category[2].equals(addr.substring(i, i+category[2].length()))){
					num=DAUM_NEWS_REALTIME;
					break;
				}
				if(category[3].equals(addr.substring(i, i+category[3].length()))){
					num=DAUM_NEWS_SOCIETY;
					break;
				}
				if(category[4].equals(addr.substring(i, i+category[4].length()))){
					num=DAUM_NEWS_POLICY;
					break;
				}
				if(category[5].equals(addr.substring(i, i+category[5].length()))){
					num=DAUM_NEWS_ECONOMY;
					break;
				}
				if(category[6].equals(addr.substring(i, i+category[6].length()))){
					num=DAUM_NEWS_WORLD;
					break;
				}
				if(category[7].equals(addr.substring(i, i+category[7].length()))){
					num=DAUM_NEWS_IT;
					break;
				}
				if(category[8].equals(addr.substring(i, i+category[8].length()))){
					num=DAUM_NEWS_CULTURE;
					break;
				}
				if(category[9].equals(addr.substring(i, i+category[9].length()))){
					num=DAUM_NEWS_ISSUE;
					break;
				}
			}
			break;
		}

		String source = str, result="";
		boolean findArticleStart = false;
		int ArticleStart=0, ArticleEnd=0;
		String start="", end="";
		
		switch(num){
		// naver
		case NAVER_NEWS_SPORTS:	
			start="<div class=\"newsct_body\" id=\"articleContent\">";
			end = "<!--";
			break;
		case NAVER_NEWS_POLICY:
		case NAVER_NEWS_ECONOMY:
		case NAVER_NEWS_SOCIETY:
		case NAVER_NEWS_LIFE:
		case NAVER_NEWS_WORLD:
		case NAVER_NEWS_IT:
		case NAVER_NEWS_ENTERTAINMENT:
		case NAVER_NEWS_ISSUE:
			start="<div class=\"newsct_body fs2\" id=\"contents\">";
			end = "<!--";
			break;
			
			// daum
		case DAUM_NEWS_SPORTS:
			start="<div class=\"News_content\" id=\"news_content\">";
			end = "<!--";
			break;
		case DAUM_NEWS_ENTERTAINMENT:
			start="<div id=\"newsContents\">";
			end = "<script>";
			break;
		case DAUM_NEWS_POLICY:
		case DAUM_NEWS_ECONOMY:
		case DAUM_NEWS_SOCIETY:
		case DAUM_NEWS_CULTURE:
		case DAUM_NEWS_WORLD:
		case DAUM_NEWS_IT:
		case DAUM_NEWS_REALTIME:
		case DAUM_NEWS_ISSUE:
			start="<div class=\"txt_news\" id=\'newsContents\'>";
			end = "<script";
			break;		
		}

		for(int i=0; i<source.length()-start.length(); i++){
			if(!findArticleStart && start.equals(source.substring(i, i+start.length()))){
				ArticleStart = i;
				findArticleStart = true;
			}
			if(findArticleStart && end.equals(source.substring(i, i+end.length()))){
				ArticleEnd = i;
				break;
			}
		}
		
		result = source.replace(source.substring(ArticleEnd, source.length()), "");
		result = result.replace(result.substring(0, ArticleStart + start.length()), "");

		
		result = WHtmlConverter.EraseTag(result);
		
		return result;
	}
}
