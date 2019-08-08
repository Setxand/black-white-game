package com.blackonwhite.client;

import com.blackonwhite.config.TelegramUrl;
import org.springframework.stereotype.Component;

@Component
public class TelegramClient extends telegram.client.TelegramClient {

	private final TelegramUrl telegramUrl;

	public TelegramClient(TelegramUrl telegramUrl) {
		super(telegramUrl.getServer(), telegramUrl.getWebhook(), telegramUrl.getTelegramUrls());
		this.telegramUrl = telegramUrl;
	}
}
