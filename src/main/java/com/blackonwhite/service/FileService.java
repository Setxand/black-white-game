package com.blackonwhite.service;

import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.Card;
import com.blackonwhite.repository.CardRepository;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import telegram.Message;
import telegram.Update;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

	private static final Logger logger = Logger.getLogger(FileService.class);
	private static final String[] cardsHeaders = {"card_type", "name"};
	private final CardRepository cardRepo;
	private final TelegramClient telegramClient;

	public FileService(CardRepository cardRepo, TelegramClient telegramClient) {
		this.cardRepo = cardRepo;
		this.telegramClient = telegramClient;
	}

	public void createXLFile(Message message) {

		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Cards");

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 17);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(headerFont);
		Row headRow = sheet.createRow(0);

		for (int i = 0; i < 2; i++) {
			Cell cell = headRow.createCell(i);
			cell.setCellValue(cardsHeaders[i]);
			cell.setCellStyle(cellStyle);
		}

		List<Card> all = cardRepo.findAll();
		int rowNum = 1;
		for (Card card : all) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(card.getCardType().name());
			row.createCell(1).setCellValue(card.getName());
		}

		for (int i = 0; i < 2; i++) {
			sheet.autoSizeColumn(i);
		}

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream("cards.xlsx");
		} catch (FileNotFoundException e) {
			logger.warn("File error", e);
		}

		try {
			workbook.write(fileOutputStream);
			assert fileOutputStream != null;
			fileOutputStream.close();
			workbook.close();

		} catch (IOException e) {
			logger.warn("Failed to create XLSX file: ", e);
		}

		telegramClient.sendFile(message, new FileSystemResource("cards.xlsx"));
	}

	public void loadCardPack(Update update) {
		Map<String, Object> document = telegramClient.getDocument(update.getMessage().document.fileId,
				update.getMessage());

		Map<String, Object> result = (Map<String, Object>) document.get("result");
		String path = (String) result.get("file_path");
		byte[]bytes = telegramClient.loadDoc(update.getMessage(), path);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(byteArrayInputStream);
		} catch (IOException e) {
			logger.warn("Failed to get work book: ", e);
		}

		assert workbook != null;
		Sheet sheet = workbook.getSheet("Cards");
		if (sheet == null) throw new BotException("Invalid file, create first", update.getMessage().getChat().getId());

		cardRepo.deleteAll();

		int i = 1;
		while (sheet.getRow(i) != null) {
			createCard(sheet.getRow(i));
			i++;
		}

		telegramClient.simpleMessage("Card pack Replaced", update.getMessage());
	}

	private void createCard(Row row) {
		Card card = new Card();
		card.setCardType(Card.CardType.valueOf(row.getCell(0).getStringCellValue()));
		card.setName(row.getCell(1).getStringCellValue());
		cardRepo.saveAndFlush(card);
	}
}
