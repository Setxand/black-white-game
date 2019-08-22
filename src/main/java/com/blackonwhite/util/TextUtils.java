package com.blackonwhite.util;

import com.blackonwhite.model.User;
import telegram.Message;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextUtils {

	public static String getResourseMessage(User user, String key) {
		return ResourceBundle.getBundle("dictionary", user.getLocale()).getString(key);
	}

	public static String getResourseMessage(Message message, String key) {
		return ResourceBundle.getBundle("dictionary",
				new Locale(message.getFrom().getLanguageCode())).getString(key);////todo change all to USER
	}
}
