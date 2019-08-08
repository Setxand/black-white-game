package com.blackonwhite.service;

import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.Room;
import com.blackonwhite.repository.RoomRepository;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import java.util.ResourceBundle;

@Service
public class RoomService {

	private final RoomRepository roomRepo;
	private final UserService userService;

	public RoomService(RoomRepository roomRepo, UserService userService) {
		this.roomRepo = roomRepo;
		this.userService = userService;
	}

	@Transactional
	public void createRoom(Message message) {
		Optional<Room> roomOpt = roomRepo.findById(message.getChat().getId());

		if (!roomOpt.isPresent()) {

			Room room = new Room();
			room.setHostId(message.getChat().getId());
			roomRepo.save(room);

		} else {
			throw new BotException(ResourceBundle.getBundle("dictionary",
					new Locale(message.getFrom().getLanguageCode())).getString("ROOM_EXISTS_ERROR"),
					message.getChat().getId());
		}

	}

	public void deleteRoom(Message message) {
		roomRepo.deleteById(message.getChat().getId());
	}
}
