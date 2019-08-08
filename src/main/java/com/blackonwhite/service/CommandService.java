package com.blackonwhite.service;

import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.User;
import com.blackonwhite.model.UserStatus;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class CommandService {

	private final TelegramClient telegramClient;
	private final UserService userService;
	private final RoomService roomService;

	public CommandService(TelegramClient telegramClient, UserService userService, RoomService roomService) {
		this.telegramClient = telegramClient;
		this.userService = userService;
		this.roomService = roomService;
	}


	@Transactional
	public void command(Message message) {
		String command = message.getText();

		switch (command) {
			case "/start":

				User user = userService.createUser(message);
				telegramClient.simpleMessage(
						String.format(getResourseMessage(message, "HELLO_MESSAGE"), user.getName()), message);
				break;

			case "/createblackcard":
				createCard(UserStatus.CREATE_WHITE, message);
				break;

			case "/createwhitecard":
				createCard(UserStatus.CREATE_BLACK, message);
				break;

			case "/createroom":
				roomService.createRoom(message);
				telegramClient.simpleMessage(getResourseMessage(message, "ROOM_ID") +
						message.getChat().getId(), message);

				break;

			case "/deleteroom":
				roomService.deleteRoom(message);
				String roomDeletedText = getResourseMessage(message, "ROOM_DELETED");
				telegramClient.simpleMessage(roomDeletedText, message);
				break;

				default: throw new BotException(getResourseMessage(message, "UNKNOWN_COMMAND"), message.getChat().getId());
		}

	}

	private String getResourseMessage(Message message, String key) {
		return ResourceBundle.getBundle("dictionary",
				new Locale(message.getFrom().getLanguageCode())).getString(key);
	}

	private void createCard(UserStatus status, Message message) {
		userService.changeUserStatus(message, status);
		telegramClient.simpleMessage(ResourceBundle
				.getBundle("dictionary", new Locale(message.getFrom().getLanguageCode()))
				.getString("CARD_NAME"), message);
	}
}
