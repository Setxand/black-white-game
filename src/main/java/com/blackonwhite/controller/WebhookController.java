package com.blackonwhite.controller;

import com.blackonwhite.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import telegram.Update;

@RestController
public class WebhookController {

	@Autowired DirectionService directionService;

	@PostMapping("/webhook")
	public void telegramWebhook(@RequestBody Update update) {
		directionService.directUpdateForCommon(update);
	}

}
