package com.blackonwhite.service;

import com.blackonwhite.client.Platform;
import org.springframework.stereotype.Service;
import telegram.Update;

@Service
public class DirectionService {

	private final MessageProcessor messageProcessor;
	private final CallbackQueryProcessor callbackQueryProcessor;

	public DirectionService(MessageProcessor messageProcessor, CallbackQueryProcessor callbackQueryProcessor) {
		this.messageProcessor = messageProcessor;
		this.callbackQueryProcessor = callbackQueryProcessor;
	}

	public void directUpdateForCommon(Update update) {



		if (update.getCallBackQuery() != null) {
			update.getCallBackQuery().getMessage().setPlatform(Platform.COMMON);
			callbackQueryProcessor.parseCallBackQuery(update.getCallBackQuery());
		} else if (update.getMessage() != null) {
			update.getMessage().setPlatform(Platform.COMMON);
			messageProcessor.parseMessage(update.getMessage());
		}
	}
}
