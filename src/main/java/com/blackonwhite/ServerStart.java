package com.blackonwhite;

import com.blackonwhite.client.TelegramClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerStart {

	private final TelegramClient client;

	public ServerStart(TelegramClient client) {
		this.client = client;
	}

	@PostConstruct
	public void init() {
		client.setWebHooks();
	}

}
