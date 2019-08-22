package com.blackonwhite.service;

import com.blackonwhite.model.User;
import com.blackonwhite.model.UserStatus;
import com.blackonwhite.repository.UserRepository;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.Locale;

@Service
public class UserService {

	private final UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Transactional
	public User createUser(Message message) {
		return userRepo.findById(message.getChat().getId()).orElseGet(() -> {
			User user = new User();
			user.setChatId(message.getChat().getId());
			user.setName(message.getChat().getFirstName() + " " + message.getChat().getLastName());
			user.setRole(User.Role.USER);

			if (message.getFrom().getLanguageCode() != null) {
				user.setLocale(new Locale(message.getFrom().getLanguageCode()));
			}

			return userRepo.save(user);
		});
	}

	public User getUser(Integer id) {
		return userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
	}

	@Transactional
	public User changeUserStatus(Message message, UserStatus status) {
		User user = getUser(message.getChat().getId());
		user.setStatus(status);
		return user;
	}
}
