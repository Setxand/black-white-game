package com.blackonwhite;

import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.User;
import com.blackonwhite.util.TextUtils;
import telegram.Message;


public class Access {

	public static void inTheGame(User user, Message message) {

		if (user.getRoomId() != null && !message.getText().equals("/exittheroom")
				&& hostCondition(user, message.getText())) {

			throw new BotException(TextUtils.getResourseMessage(message, "IN_THE_GAME"),
					message.getChat().getId());
		}
	}

	private static boolean hostCondition(User user, String text) {
		return !(user.getChatId().equals(user.getRoomId()) && text.equals("/startthegame"));
	}

	public static void admin(User user) {
		try {
			throwIfFalse(user.getRole() == User.Role.ADMIN, "Forbidden");
		} catch (IllegalArgumentException ex) {
			throw new BotException("Forbidden", user.getChatId());
		}
	}

	private static void throwIfFalse(boolean b, String message) {
		if (!b) throw new IllegalArgumentException(message);
	}
}
