package com.blackonwhite.service;

import com.blackonwhite.client.Platform;
import com.blackonwhite.client.TelegramClient;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.Room;
import com.blackonwhite.model.User;
import com.blackonwhite.payload.CallBackPayload;
import com.blackonwhite.util.PayloadUtils;
import com.blackonwhite.util.TextUtils;
import org.springframework.stereotype.Component;
import telegram.CallBackQuery;
import telegram.Chat;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CallbackQueryProcessor {

	private final TelegramClient telegramClient;
	private final RoomService roomService;
	private final UserService userService;
	private final CardService cardService;

	public CallbackQueryProcessor(TelegramClient telegramClient, RoomService roomService, UserService userService,
								  CardService cardService) {
		this.telegramClient = telegramClient;
		this.roomService = roomService;
		this.userService = userService;
		this.cardService = cardService;
	}

	@Transactional
	public void parseCallBackQuery(CallBackQuery callBackQuery, User user) {

		switch (CallBackPayload.valueOf(PayloadUtils.getCommonPayload(callBackQuery.getData()))) {

			case QUESTION:
				simpleQuestion(callBackQuery);
				break;

			case START_GAME:
				List<User> userQueue = roomService.getRoom(user.getRoomId()).getUserQueue();
				user.setBlackCardMetaInf(callBackQuery.getMessage().getMessageId().toString());

				telegramClient.simpleMessage("Game was started", callBackQuery.getMessage());////todo ditionary
				telegramClient.editInlineButtons(null, callBackQuery.getMessage());
				telegramClient.gameInterfaceForWhite(userQueue, callBackQuery.getMessage(),
						cardService.getCard(user.getBlackCardId()));
				break;

			case WHITE_CARD_CHOICE:
				whiteCardChoice(callBackQuery, user);
				break;

			case BLACK_CARD_CHOICE:
				blackCardChoice(callBackQuery, user);
				break;

			default:
				throw new IllegalArgumentException("Invalid Callback query DATA");
		}

	}

	private void blackCardChoice(CallBackQuery callBackQuery, User user) {
		Message message = callBackQuery.getMessage();
		message.getFrom().setLanguageCode(callBackQuery.getFrom().getLanguageCode());
		Room room = roomService.getRoom(user.getRoomId());

		if (room.getUserQueue().size() == room.getPickedCards().size()) {
			String cardId = PayloadUtils.getParams(callBackQuery.getData())[0];
			String playerId = room.getPickedCards().get(cardId);

			User player = userService.getUser(Integer.valueOf(playerId));
			player.setVinRate(player.getVinRate() + 1);

			room.getUserQueue().forEach(u -> {
				message.getChat().setId(u.getChatId());
				telegramClient.simpleMessage(
						String.format(TextUtils.getResourseMessage(message, "WIN_RATE"),
								winRate(room.getUserQueue())), message);
			});


			message.getChat().setId(user.getChatId());
			telegramClient.deleteMessage(message);

			room.setPickedCards(new HashMap<>());

			User nextBlackCardUser = roomService.getNextBlackCardUser(message, user);
			telegramClient.gameInterface(nextBlackCardUser, message,
					cardService.getCard(nextBlackCardUser.getBlackCardId()));

		} else {
			telegramClient.simpleMessage(TextUtils.getResourseMessage(message, "ANSWER_WAIT"), message);
		}
	}

	private String winRate(List<User> userQueue) {
		return userQueue.stream().map(u -> u.getName() + " - " + u.getVinRate())
				.collect(Collectors.joining("\n"));
	}

	private void whiteCardChoice(CallBackQuery callBackQuery, User user) {
		String[] params = PayloadUtils.getParams(callBackQuery.getData());
		String cardId = params[0];
		Room room = roomService.getRoom(user.getRoomId());

		User blackCardUser = userService.getUser(room.getBlackCardPlayerId());

		room.getPickedCards().put(cardId, user.getChatId().toString());

		user.getCards().remove(cardService.getCard(cardId));
		user.getCards().add(cardService.getRandomCard(Card.CardType.WHITE, user.getRoomId()));

		telegramClient.gameInterfaceBorBlackCard(blackCardUser,
				room.getPickedCards().entrySet().stream()
						.collect(Collectors.toMap(c -> c.getValue(), c -> cardService.getCard(c.getKey()))));

		telegramClient.deleteMessage(callBackQuery.getMessage());
	}

	private void simpleQuestion(CallBackQuery callBackQuery) {
		String[] params = PayloadUtils.getParams(callBackQuery.getData());

		switch (params[0]) {

			case "CONNECTION_QUESTION":
				if (isPositive(params[2])) {

					User player = userService.getUser(Integer.parseInt(params[1]));
					List<User> users = roomService
							.addPlayer(callBackQuery.getMessage().getChat().getId(), player);

					Message message = new Message(new Chat());
					message.setPlatform(Platform.COMMON);
					users.forEach(u -> {
						message.getChat().setId(u.getChatId());
						telegramClient
								.simpleMessage("User " + player.getName() + " has connected to the game.", message);///todo dictionary
					});

					telegramClient.deleteMessage(callBackQuery.getMessage());

				} else {
					telegramClient.deleteMessage(callBackQuery.getMessage());
					callBackQuery.getMessage().getChat().setId(Integer.parseInt(params[1]));
					telegramClient.simpleMessage("Your request to room was declined", callBackQuery.getMessage());
				}
				break;

			default:
				throw new IllegalArgumentException("Invalid callback payload");
		}

	}

	private boolean isPositive(String param) {
		return Integer.parseInt(param) == 1;
	}
}
