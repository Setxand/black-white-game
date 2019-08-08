package com.blackonwhite.service;

import com.blackonwhite.client.Platform;
import org.springframework.stereotype.Service;
import telegram.Update;

@Service
public class DirectionService {

	private final MessageProcessor messageProcessor;

	public DirectionService(MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public void directUpdateForCommon(Update update) {

		update.getMessage().setPlatform(Platform.COMMON);

		if (update.getMessage() != null) {
			messageProcessor.parseMessage(update.getMessage());
		}
	}
}
