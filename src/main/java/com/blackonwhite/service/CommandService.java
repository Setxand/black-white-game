package com.blackonwhite.service;

import com.blackonwhite.Access;
import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.User;
import com.blackonwhite.model.UserStatus;
import com.blackonwhite.util.TextUtils;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.blackonwhite.util.TextUtils.getResourseMessage;

@Service
public class CommandService {

	private final TelegramClient telegramClient;
	private final RoomService roomService;
	private final CardService cardService;
	private final FileService fileService;

	public CommandService(TelegramClient telegramClient, RoomService roomService, CardService cardService,
						  FileService fileService) {
		this.telegramClient = telegramClient;
		this.roomService = roomService;
		this.cardService = cardService;
		this.fileService = fileService;
	}

	@Transactional
	public void command(Message message, User user) {
		String command = message.getText();
		user.setStatus(null);

		Access.inTheGame(user, message);
		switch (command) {

			case "/start":

				telegramClient.simpleMessage(
						String.format(getResourseMessage(message, "HELLO_MESSAGE"), user.getName()), message);
				break;

			case "/createblackcard":
				createCard(UserStatus.CREATE_BLACK, message, user);
				break;

			case "/createwhitecard":
				createCard(UserStatus.CREATE_WHITE, message, user);
				break;

			case "/createroom":
				roomService.createRoom(message, user);
				telegramClient.simpleMessage(getResourseMessage(message, "ROOM_ID") +
						message.getChat().getId(), message);
				break;

			case "/deleteroom":
				List<User> users = roomService.deleteRoom(message, user);

				users.forEach(u -> {
					message.getChat().setId(u.getChatId());
					telegramClient.simpleMessage(getResourseMessage(user, "ROOM_DELETED"), message);
				});

//				message.getChat().setId(user.getChatId());
//				String roomDeletedText = getResourseMessage(message, "ROOM_DELETED");
//				telegramClient.simpleMessage(roomDeletedText, message);
				break;

			case "/connecttorooom":
				user.setStatus(UserStatus.ROOM_CONNECTION);
				telegramClient.simpleMessage(getResourseMessage(user, "ROOM_CONNECTION"), message);
				break;

			case "/startthegame":
				roomService.startTheGame(message);
				User nextBlackCardUser = roomService.getNextBlackCardUser(message, user);
				telegramClient.gameInterface(nextBlackCardUser, message,
						cardService.getCard(nextBlackCardUser.getBlackCardId()));
				break;

			case "/exittheroom":
				exitTheRoom(message, user);
				break;

			case "/createFile":
				Access.admin(user);
				fileService.createXLFile(message);
				break;

			default:
				throw new BotException(getResourseMessage(message, "UNKNOWN_COMMAND"), message.getChat().getId());
		}

	}

	private void exitTheRoom(Message message, User user) {
		if (user.getRoomId() == null) botEx(message, user);

		List<User> users = roomService.deleteRoomForUser(user);

		users.forEach(u -> {
			if (!u.getChatId().equals(user.getChatId())) {
				message.getChat().setId(u.getChatId());
				telegramClient.simpleMessage(String.format(TextUtils
						.getResourseMessage(user, "USER_KICKED"), user.getName()), message);
			}
		});

		message.getChat().setId(user.getChatId());
		telegramClient.simpleMessage(TextUtils.getResourseMessage(user, "DONE"), message);
	}

	private void botEx(Message message, User user) {
		throw new BotException(TextUtils.getResourseMessage(user, "ILLEGAL_OP"), message.getChat().getId());
	}

	private void createCard(UserStatus status, Message message, User user) {
		user.setStatus(status);
		telegramClient.simpleMessage(ResourceBundle
				.getBundle("dictionary", new Locale(message.getFrom().getLanguageCode()))
				.getString("CARD_NAME"), message);
	}
}
