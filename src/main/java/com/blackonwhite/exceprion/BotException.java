package com.blackonwhite.exceprion;


import lombok.Getter;

@Getter
public class BotException extends RuntimeException {

	private final Integer chatId;

	public BotException(String message, Integer chatId) {
		super(message);
		this.chatId = chatId;
	}
}
