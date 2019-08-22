package com.blackonwhite.service;

import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.Room;
import com.blackonwhite.model.User;
import com.blackonwhite.repository.RoomRepository;
import com.blackonwhite.repository.UserRepository;
import com.blackonwhite.util.TextUtils;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class RoomService {

	private final RoomRepository roomRepo;
	private final CardService cardService;
	private final UserRepository userRepo;

	public RoomService(RoomRepository roomRepo, CardService cardService, UserRepository userRepo) {
		this.roomRepo = roomRepo;
		this.cardService = cardService;
		this.userRepo = userRepo;
	}


	@Transactional
	public void createRoom(Message message, User host) {
		Optional<Room> roomOpt = roomRepo.findById(message.getChat().getId());

		if (!roomOpt.isPresent()) {

			Room room = new Room();
			room.setHostId(message.getChat().getId());
			roomRepo.save(room);
			addPlayer(host.getChatId(), host);

		} else {
			throw new BotException(ResourceBundle.getBundle("dictionary",
					new Locale(message.getFrom().getLanguageCode())).getString("ROOM_EXISTS_ERROR"),
					message.getChat().getId());
		}

	}

	public List<User> deleteRoom(Message message) {
		Room room = null;

		try {
			room = getRoom(message.getChat().getId());
		} catch (IllegalArgumentException ex) {
			throw new BotException(TextUtils.getResourseMessage(message, "ILLEGAL_OP"),
					message.getChat().getId());
		}

		ArrayList<User> users = new ArrayList<>(room.getUserQueue());

		for (Iterator<User> iterator = room.getUserQueue().iterator(); iterator.hasNext(); ) {
			User user = iterator.next();
			user.setCards(new LinkedList<>());
			if (user.getBlackCardId() != null) user.setBlackCardId(null);
			user.setVinRate(0);
			user.setRoomId(null);
		}

		room.setUserQueue(new LinkedList<>());
		cardService.deleteRoom(message.getChat().getId());
		roomRepo.deleteById(message.getChat().getId());

		return users;
	}

	public List<User> deleteRoomForUser(User user) {

		Room room = getRoom(user.getRoomId());

		room.getUserQueue().remove(user);

		user.setCards(new LinkedList<>());

		if (user.getBlackCardId() != null) user.setBlackCardId(null);

		user.setVinRate(0);
		user.setRoomId(null);
		return room.getUserQueue();
	}

	@Transactional
	public List<User> addPlayer(Integer roomId, User player) {
		Room room = getRoom(roomId);

		if (room.getUserQueue().contains(player)) {
			throw new BotException("User already exists in the room", roomId);///todo exc in dictionary
		}

		room.getUserQueue().add(0, player);
		player.setRoomId(roomId);

		return room.getUserQueue();
	}

	@Transactional
	public User getNextBlackCardUser(Message message, User user) {
		Integer roomId = user.getRoomId();

		Room room = getRoom(roomId);
		List<User> userQueue = room.getUserQueue();

		if (userQueue.size() < 1) ///todo in real case min is 3!
			throw new BotException(TextUtils.getResourseMessage(message, "PLAYERS_COUNT_ERROR"), roomId);

		User prevUser = userQueue.get(0);

		if (prevUser.getBlackCardId() != null) {
			prevUser.setBlackCardId(null);
			userQueue.add(userQueue.size(), prevUser);
			userQueue.remove(0);
		}

		User newUser = userQueue.get(0);
		room.setBlackCardPlayerId(newUser.getChatId());
		newUser.setBlackCardId(cardService.getRandomCard(Card.CardType.BLACK, roomId).getId());

		return userRepo.saveAndFlush(newUser);
	}

	@Transactional
	public Room startTheGame(Message message) {
		Integer roomId = message.getChat().getId();
		Room room = roomRepo.findById(roomId)
				.orElseThrow(() -> new BotException(TextUtils.getResourseMessage(message, "CREATE_ROOM_FIRST"), roomId));

		List<User> userQueue = room.getUserQueue();
		userQueue.forEach(u -> {

			for (int i = 0; i < 5; i++) {
				u.getCards().add(cardService.getRandomCard(Card.CardType.WHITE, roomId));

			}
		});

		return room;
	}

	public Room getRoom(Integer roomId) {
		return roomRepo.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
	}
}
