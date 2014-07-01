package com.gamerecorder.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	
	public static String formatDateTime(Date date){
		return sdf.format(date);
	}
	
	public static String formatDate(Date date){
		return sdf.format(date).split(" ")[0];
	}
}


