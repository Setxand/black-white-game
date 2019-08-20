package com.blackonwhite.service;

import com.blackonwhite.client.Platform;
import com.blackonwhite.model.User;
import org.springframework.stereotype.Service;
import telegram.Update;

import javax.transaction.Transactional;

@Service
public class DirectionService {

	private final MessageProcessor messageProcessor;
	private final CallbackQueryProcessor callbackQueryProcessor;
	private final UserService userService;
	private final DocumentProcessor documentProcessor;

	public DirectionService(MessageProcessor messageProcessor, CallbackQueryProcessor callbackQueryProcessor,
							UserService userService, DocumentProcessor documentProcessor) {
		this.messageProcessor = messageProcessor;
		this.callbackQueryProcessor = callbackQueryProcessor;
		this.userService = userService;
		this.documentProcessor = documentProcessor;
	}


	@Transactional
	public void directUpdateForCommon(Update update) {

		if (update.getCallBackQuery() != null) {
			User user = userService.createUser(update.getCallBackQuery().getMessage());

			update.getCallBackQuery().getMessage().setPlatform(Platform.COMMON);
			callbackQueryProcessor.parseCallBackQuery(update.getCallBackQuery(), user);


		} else if (update.getMessage().document != null) {
			update.getMessage().setPlatform(Platform.COMMON);
			documentProcessor.loadCardPack(update);


		} else if (update.getMessage() != null) {
			User user = userService.createUser(update.getMessage());

			update.getMessage().setPlatform(Platform.COMMON);
			messageProcessor.parseMessage(update.getMessage(), user);

		}
	}
}
