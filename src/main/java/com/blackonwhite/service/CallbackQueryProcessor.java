package com.blackonwhite.service;

import com.blackonwhite.client.Platform;
import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.model.User;
import com.blackonwhite.util.PayloadUtils;
import org.springframework.stereotype.Component;
import telegram.CallBackQuery;
import telegram.Chat;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class CallbackQueryProcessor {

	private final TelegramClient telegramClient;
	private final RoomService roomService;
	private final UserService userService;

	public CallbackQueryProcessor(TelegramClient telegramClient, RoomService roomService, UserService userService) {
		this.telegramClient = telegramClient;
		this.roomService = roomService;
		this.userService = userService;
	}

	@Transactional
	public void parseCallBackQuery(CallBackQuery callBackQuery, User user) {

		switch (PayloadUtils.getCommonPayload(callBackQuery.getData())) {
			case "QUESTION":
				simpleQuestion(callBackQuery);
				break;

			default:
				throw new IllegalArgumentException("Invalid Callback query DATA");
		}

	}

	private void simpleQuestion(CallBackQuery callBackQuery) {
		String[] params = PayloadUtils.getParams(callBackQuery.getData());

		switch (params[0]) {

			case "CONNECTION_QUESTION":
				if (isPositive(params[2])) {

					User player = userService.getUser(Integer.parseInt(params[1]));
					List<User> users = roomService
							.addPlayer(callBackQuery.getMessage().getChat().getId(), player);

					Message message = new Message(new Chat());
					message.setPlatform(Platform.COMMON);
					users.forEach(u -> {
						message.getChat().setId(u.getChatId());
						telegramClient
								.simpleMessage("User " + player.getName() + " has connected to the game.", message);///todo dictionary
					});
					player.setRoomId(message.getChat().getId());

				} else {
					callBackQuery.getMessage().getChat().setId(Integer.parseInt(params[1]));
					telegramClient.simpleMessage("Your request to room was declined", callBackQuery.getMessage());
					telegramClient.editInlineButtons(null, callBackQuery.getMessage());
				}

				telegramClient.editInlineButtons(null, callBackQuery.getMessage());

				break;

			default:
				throw new IllegalArgumentException("Invalid callback payload");
		}

	}

	private boolean isPositive(String param) {
		return Integer.parseInt(param) == 1;
	}
}
