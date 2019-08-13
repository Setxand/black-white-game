package com.blackonwhite.util;

import telegram.Message;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextUtils {

	public static String getResourseMessage(Message message, String key) {
		return ResourceBundle.getBundle("dictionary",
				new Locale(message.getFrom().getLanguageCode())).getString(key);
	}
}
