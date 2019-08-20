package com.blackonwhite.service;

import com.blackonwhite.client.TelegramClient;
import org.springframework.stereotype.Service;
import telegram.Update;

import java.io.InputStream;
import java.util.Map;

@Service
public class DocumentProcessor {

	private final TelegramClient telegramClient;
	private final FileService fileService;

	public DocumentProcessor(TelegramClient telegramClient, FileService fileService) {
		this.telegramClient = telegramClient;
		this.fileService = fileService;
	}

	public void loadCardPack(Update update) {
		Map<String, Object> document = telegramClient.getDocument(update.getMessage().document.fileId, update.getMessage());
		Map<String, Object> result = (Map<String, Object>) document.get("result");
		String path = (String) result.get("file_path");

		InputStream stream = telegramClient.loadDoc(update.getMessage(), path);

	}

}
