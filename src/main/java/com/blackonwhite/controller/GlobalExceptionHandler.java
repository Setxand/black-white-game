package com.blackonwhite.controller;

import com.blackonwhite.client.Platform;
import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.exceprion.BotException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import telegram.Chat;
import telegram.Message;


@ControllerAdvice
public class GlobalExceptionHandler {

	@Autowired TelegramClient telegramClient;

	private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BotException.class)
	public void handleBotException(BotException ex) {
		logger.warn("Error: ", ex);
		Message message = new Message(new Chat(ex.getChatId()));
		message.setPlatform(Platform.COMMON);
		telegramClient.simpleMessage(ex.getMessage(), message);
	}
}
