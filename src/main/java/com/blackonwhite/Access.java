package com.blackonwhite;

import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.User;
import com.blackonwhite.util.TextUtils;
import telegram.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Access {

	private static final Set<String> hostCommands = new HashSet(Arrays.asList("/startthegame", "/deleteroom"));

	public static void inTheGame(User user, Message message) {
		if (user.getRoomId() != null && !message.getText().equals("/exittheroom")
				&& hostCondition(user, message.getText())) {

			throw new BotException(TextUtils.getResourseMessage(message, "IN_THE_GAME"),
					message.getChat().getId());
		}
	}

	private static boolean hostCondition(User user, String text) {
		return !(user.getChatId().equals(user.getRoomId()) && hostCommands.contains(text));
	}

	public static void admin(User user) {
		if (user.getRole() != User.Role.ADMIN) {
			throw new BotException("Forbidden", user.getChatId());
		}
	}
}
