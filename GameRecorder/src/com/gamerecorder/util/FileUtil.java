package com.gamerecorder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

public class FileUtil {

	public static Object getSettingsFile(Context ctx){
		Object result = null;
		File dataFile = new File(ctx.getFilesDir(), Constants.SETTING_DATA_FILE_NAME);
		if (!dataFile.exists())
			return result;

		FileInputStream inputStream;
		try {
			inputStream = ctx.openFileInput(Constants.SETTING_DATA_FILE_NAME);
			ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
			result = objInputStream.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void writeSettingsFile(Context ctx,Object content){
		FileOutputStream outputStream;
		try {
			outputStream = ctx.openFileOutput(Constants.SETTING_DATA_FILE_NAME,Activity.MODE_PRIVATE);

			ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
			objOutputStream.writeObject(content);
			objOutputStream.close();

			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeGameHistoryFile(Context ctx,List<Object> content){
		FileOutputStream outputStream;
		try {
			outputStream = ctx.openFileOutput(Constants.GAME_HISTORY_DATA_FILE_NAME,Activity.MODE_PRIVATE);

			ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
			objOutputStream.writeObject(content);
			objOutputStream.close();

			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Object> getGameHistoryFile(Context ctx){
		List<Object> result = new ArrayList<Object>();
		File dataFile = new File(ctx.getFilesDir(), Constants.GAME_HISTORY_DATA_FILE_NAME);
		if (!dataFile.exists())
			return result;

		FileInputStream inputStream;
		try {
			inputStream = ctx.openFileInput(Constants.GAME_HISTORY_DATA_FILE_NAME);
			ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
						
			result = (List)objInputStream.readObject();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
}
