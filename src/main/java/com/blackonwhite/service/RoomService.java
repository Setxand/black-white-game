package com.blackonwhite.service;

import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.Room;
import com.blackonwhite.model.User;
import com.blackonwhite.repository.RoomRepository;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class RoomService {

	private final RoomRepository roomRepo;
	private final UserService userService;
	private final CardService cardService;

	public RoomService(RoomRepository roomRepo, UserService userService, CardService cardService) {
		this.roomRepo = roomRepo;
		this.userService = userService;
		this.cardService = cardService;
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
		Room room = getRoom(message.getChat().getId());

		room.getUserQueue().forEach(u -> {
			u.setCards(new LinkedList<>());
			if (u.getBlackCard() != null) u.setBlackCard(null);
			u.setVinRate(0);
		});

		cardService.deleteRoom(message.getChat().getId());
		roomRepo.deleteById(message.getChat().getId());
	}

	@Transactional
	public List<User> addPlayer(Integer roomId, User player) {
		Room room = getRoom(roomId);

		if (room.getUserQueue().contains(player)) {
			throw new BotException("User already exists in the room", roomId);
		}

		room.getUserQueue().add(0, player);

		return room.getUserQueue();
	}

	@Transactional
	public User getNextBlackCardUser(Integer roomId) {
		Room room = getRoom(roomId);
		List<User> userQueue = room.getUserQueue();
		User prevUser = userQueue.get(0);

		if (prevUser.getBlackCard() != null) {
			prevUser.setBlackCard(null);
			userQueue.add(userQueue.size(), prevUser);
			userQueue.remove(0);
		}

		User newUser = userQueue.get(0);
		newUser.setBlackCard(cardService.getRandomCard(roomId, Card.CardType.BLACK));

		return userQueue.get(0);
	}

	@Transactional
	public List<User> startTheGame(Integer roomId) {
		Room room = getRoom(roomId);
		List<User> userQueue = room.getUserQueue();

		userQueue.forEach(u -> {

			for (int i = 0; i < 5; i++) {
				u.getCards().add(cardService.getRandomCard(roomId, Card.CardType.WHITE));
			}

		});

		return userQueue;
	}

	private Room getRoom(Integer roomId) {
		return roomRepo.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
	}
}
