package com.example.summaryproject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.os.Environment;
import android.provider.MediaStore;

public class WFileLoader{

	// Variables
	static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator;
	static ArrayList<String> fileItemList;
	static ArrayList<String> fileDirList;
	static File f;
	static File[] files;
	
		
	public static void getDir(String dirPath){
		String type = MediaStore.Files.FileColumns.MIME_TYPE +"=?";
		fileItemList = new ArrayList<String>();
		fileDirList = new ArrayList<String>();
		
		f = new File(dirPath);
		files = f.listFiles();
		
		if(!dirPath.equals(filePath)){
			fileItemList.add(filePath);
			fileDirList.add(filePath);
			
			fileItemList.add("../");
			fileDirList.add(f.getParent());
		}
		
		for(int i=0; i<files.length; i++){
			File file = files[i];
			fileDirList.add(file.getPath());
			
			if(file.isDirectory())
				fileItemList.add(file.getName() + "/");
			else
				fileItemList.add(file.getName() + " (" + file.length()/1024 + "kb)");
		}
	}
	
}

//폴더와 TXT만 받아오기
class TXTFilter implements FileFilter{
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().endsWith(".txt");
	}
}
