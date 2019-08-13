package com.blackonwhite.service;

import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.User;
import org.springframework.stereotype.Service;
import telegram.Message;


import javax.transaction.Transactional;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.blackonwhite.util.TextUtils.getResourseMessage;

@Service
public class MessageProcessor {

	private final CommandService commandService;
	private final UserService userService;
	private final CardService cardService;
	private final TelegramClient telegramClient;

	public MessageProcessor(CommandService commandService, UserService userService,
							CardService cardService, TelegramClient telegramClient) {
		this.commandService = commandService;
		this.userService = userService;
		this.cardService = cardService;
		this.telegramClient = telegramClient;
	}

	@Transactional
	public void parseMessage(Message message) {

		if (message.getText().startsWith("/")) {
			commandService.command(message);
			return;
		}

		User user = userService.getUser(message.getChat().getId());
		if (user.getStatus() != null) {
			processMessageByStatus(user, message);
		}
	}


	private void processMessageByStatus(User user, Message message) {

		switch (user.getStatus()) {
			case CREATE_WHITE:
				createCard(message, Card.CardType.WHITE);
				break;

			case CREATE_BLACK:
				createCard(message, Card.CardType.BLACK);
				break;

			case ROOM_CONNECTION:

				try {
					message.getChat().setId(Integer.parseInt(message.getText()));
				} catch (NumberFormatException ex) {
					throw new BotException("Invalid Room ID", message.getChat().getId());
				}

				telegramClient.simpleQuestion("CONNECTION_QUESTION&" + message.getChat().getId(),
						String.format(getResourseMessage(message, "CONNECTION_QUESTION"),
								message.getFrom().getFirstName() + " " + message.getFrom().getLastName()), message);

				user.setStatus(null);
				break;

			default:
				throw new IllegalArgumentException("Invalid User Status");
		}

	}

	private void createCard(Message message, Card.CardType type) {
		cardService.createCard(type, message.getText());
		telegramClient.simpleMessage(
				ResourceBundle.getBundle("dictionary", new Locale(message.getFrom().getLanguageCode()))
				.getString("DONE"), message);
		userService.changeUserStatus(message, null);
	}

}
