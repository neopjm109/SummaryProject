package com.wscompany.summary;

import java.util.ArrayList;

public class WSummary {
	// ¾Æ·¡ ÇÔ¼öÃ³·³ staticÀ» ²À ³Ö¾îÁÖ¼¼¿ä. »ı¼º ¾È½ÃÅ°°í ¹Ù·Î ÇÔ¼ö¸¦ ÀÌ¿ëÇÒ ¼ö ÀÖµµ·Ï

	String StringTitle;
	String StringBody;
	static String SummaryString;
	static ArrayList<String> Strings = new ArrayList<String>();
	
	public static void init(){
		SummaryString = "";
		Strings.clear();
	}

	public static String SummaryCheck(String str) {
		// ¹®ÀÚ¿­·Î ÀúÀå
		init();
		SummaryString = str;
		devideStrings();
		removeUnnecessaryString();
		combineStrings();

		return SummaryString;
	}

	// ¹®Àå ³ª´©±â
	public static void devideStrings() {
		String OldString = "";
		boolean OldOn = false;
		String[] tempStrings;
		tempStrings = SummaryString.split("\\.");
		for (int i = 0; i < tempStrings.length; i++) {
			if (tempStrings[i].charAt(tempStrings[i].length() - 1) >= '°¡'
					&& tempStrings[i].charAt(tempStrings[i].length() - 1) <= 'ÆR') {
				if (OldOn) {
					Strings.add(OldString + "." + tempStrings[i]);
					OldOn = false;
				} else {
					Strings.add(tempStrings[i]);
				}
			} else {
				OldString = tempStrings[i];
				OldOn = true;
			}
		}
	}

	//¹®Àå ÇÕÄ¡±â
	public static void combineStrings() {
		StringBuffer tempStr = new StringBuffer();
		for (int i = 0; i < Strings.size(); i++) {
			StringBuffer temp = new StringBuffer(Strings.get(i));
			temp.append(".\n");
			Strings.set(i, temp.toString());
	//		Log.d("naddola", Strings.get(i));
			tempStr.append(Strings.get(i));
		}
		SummaryString = tempStr.toString();
	}
	
	public static void removeUnnecessaryString() {
		String[] Keywords = {"\u201D", "\u201C", "\""};
		
		for(int i=0; i<Strings.size(); i++){
			String Str = Strings.get(i);
			for(int j=0; j<Keywords.length; j++){
				if(Str.matches(".*"+Keywords[j]+".*")){
					Strings.remove(i);
					i--;
					break;
				}
			}
		}
	}

}
