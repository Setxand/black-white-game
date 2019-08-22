package com.blackonwhite.client;

import com.blackonwhite.config.TelegramUrl;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.User;
import com.blackonwhite.payload.CallBackPayload;
import com.blackonwhite.util.PayloadUtils;
import com.blackonwhite.util.TextUtils;
import org.springframework.stereotype.Component;
import telegram.Chat;
import telegram.Markup;
import telegram.Message;
import telegram.button.InlineKeyboardButton;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.blackonwhite.payload.CallBackPayload.*;

@Component
public class TelegramClient extends telegram.client.TelegramClient {

	private final TelegramUrl telegramUrl;

	public TelegramClient(TelegramUrl telegramUrl) {
		super(telegramUrl.getServer(), telegramUrl.getWebhook(), telegramUrl.getTelegramUrls());
		this.telegramUrl = telegramUrl;
	}


	@Override
	public void simpleQuestion(String payload, String text, Message message) {

		String yes = ResourceBundle.getBundle("dictionary").getString("YES");
		String no = ResourceBundle.getBundle("dictionary").getString("NO");
		Markup buttonListMarkup = this.createButtonListMarkup(true,
				new InlineKeyboardButton(yes, PayloadUtils.createPayloadWithParams(QUESTION.name(), payload, "1")),
				new InlineKeyboardButton(no, PayloadUtils.createPayloadWithParams(QUESTION.name(), payload, "0")));
		this.sendButtons(buttonListMarkup, text, message);
	}

	public void gameInterface(User user, Message message, Card blackCard) {
		message.getChat().setId(user.getChatId());
		sendButtons(createButtonListMarkup(false,
				new InlineKeyboardButton(TextUtils.getResourseMessage(user, "START_GAME"),
						PayloadUtils.createPayloadWithParams(START_GAME.name(), user.getChatId().toString()))),
				blackCard.getName(), message);

	}

	public void gameInterfaceBorBlackCard(User blackCardUser, Map<String, Card> pickedCards) {

		Message message = new Message(new Chat(blackCardUser.getChatId()));
		message.setMessageId(Integer.valueOf(blackCardUser.getBlackCardMetaInf()));
		message.setPlatform(Platform.COMMON);

		editInlineButtons(
				createButtonListMarkup(false, pickedCards.values().stream()
						.map(card -> new InlineKeyboardButton(card.getName(),
								setPayloadParams(card.getId(),
										CallBackPayload.BLACK_CARD_CHOICE))).
								toArray(InlineKeyboardButton[]::new)), message);
	}

	public void gameInterfaceForWhite(List<User> users, Message message, Card blackCard) {
		users.forEach(u -> {

//			if (u.getBlackCard() == null) {todo
			message.getChat().setId(u.getChatId());

			sendButtons(createButtonListMarkup(false,
					u.getCards().stream().map(c -> new InlineKeyboardButton(c.getName(),
							setPayloadParams(c.getId(), WHITE_CARD_CHOICE)))
							.toArray(InlineKeyboardButton[]::new)), blackCard.getName(), message);
//			}
		});
	}

	private String setPayloadParams(String cardId, CallBackPayload payload) {
		return PayloadUtils
				.createPayloadWithParams(payload.name(), cardId);///chatId - in this case it's room id
	}
}
