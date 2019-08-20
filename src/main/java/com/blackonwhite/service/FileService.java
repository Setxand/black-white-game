package com.blackonwhite.service;

import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.model.Card;
import com.blackonwhite.repository.CardRepository;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import telegram.Message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
			logger.warn("IOEx", e);
		}

		telegramClient.sendFile(message, new FileSystemResource("cards.xlsx"));
	}
}
