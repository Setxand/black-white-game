package com.blackonwhite.service;

import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.exceprion.BotException;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.Room;
import com.blackonwhite.model.User;
import com.blackonwhite.repository.RoomRepository;
import com.blackonwhite.repository.UserRepository;
import org.springframework.stereotype.Service;
import telegram.Chat;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.*;

import static com.blackonwhite.util.TextUtils.getResourseMessage;

@Service
public class RoomService {

	private final RoomRepository roomRepo;
	private final CardService cardService;
	private final UserRepository userRepo;
	private final TelegramClient telegramClient;

	public RoomService(RoomRepository roomRepo, CardService cardService, UserRepository userRepo,
					   TelegramClient telegramClient) {
		this.roomRepo = roomRepo;
		this.cardService = cardService;
		this.userRepo = userRepo;
		this.telegramClient = telegramClient;
	}


	@Transactional
	public void createRoom(Message message, User host) {
		Optional<Room> roomOpt = roomRepo.findById(message.getChat().getId());

		if (!roomOpt.isPresent()) {

			Room room = new Room();
			room.setHostId(message.getChat().getId());
			roomRepo.save(room);
			addPlayer(message, host);

		} else {
			throw new BotException(ResourceBundle.getBundle("dictionary",
					new Locale(message.getFrom().getLanguageCode())).getString("ROOM_EXISTS_ERROR"),
					message.getChat().getId());
		}

	}

	public List<User> deleteRoom(Message message, User user) {
		Room room = null;

		try {
			room = getRoom(message.getChat().getId());
		} catch (IllegalArgumentException ex) {
			throw new BotException(getResourseMessage(user, "ILLEGAL_OP"),
					message.getChat().getId());
		}

		ArrayList<User> users = new ArrayList<>(room.getUserQueue());

		for (Iterator<User> iterator = room.getUserQueue().iterator(); iterator.hasNext(); ) {
			User u = iterator.next();
			u.setCards(new LinkedList<>());
			if (u.getBlackCardId() != null) u.setBlackCardId(null);
			u.setWinRate(0);
			u.setRoomId(null);
			deleteMetaInf(user);
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

		user.setWinRate(0);
		user.setRoomId(null);
		return room.getUserQueue();
	}

	@Transactional
	public List<User> addPlayer(Message message, User player) {
		Integer roomId = message.getChat().getId();

		Room room = getRoom(roomId);

		if (room.getUserQueue().contains(player)) {
			throw new BotException(ResourceBundle.getBundle("dictionary",
					new Locale(message.getFrom().getLanguageCode())).getString("USER_EXISTS_IN_ROOM"), roomId);
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

		if (userQueue.size() < 3)
			throw new BotException(getResourseMessage(user, "PLAYERS_COUNT_ERROR"), roomId);

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
				.orElseThrow(() -> new BotException(getResourseMessage(message, "CREATE_ROOM_FIRST"), roomId));

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

	private void deleteMetaInf(User user) {

		if (user.getWhiteCardMetaInf() != null) {
			telegramClient.deleteMessage(getUserInterfaceMessage(user.getChatId(), user.getWhiteCardMetaInf()));
		}

		if (user.getBlackCardMetaInf() != null) {
			telegramClient.deleteMessage(getUserInterfaceMessage(user.getChatId(), user.getBlackCardMetaInf()));
		}
	}

	private Message getUserInterfaceMessage(Integer chatId, String metaInf) {
		Message message = new Message(new Chat(chatId));
		message.setMessageId(Integer.valueOf(metaInf));
		return message;
	}
}
