package com.blackonwhite.client;

import com.blackonwhite.config.TelegramUrl;
import com.blackonwhite.model.Card;
import com.blackonwhite.model.User;
import com.blackonwhite.util.PayloadUtils;
import org.springframework.stereotype.Component;
import telegram.Markup;
import telegram.Message;
import telegram.button.InlineKeyboardButton;

import java.util.List;
import java.util.ResourceBundle;

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
				new InlineKeyboardButton(yes, PayloadUtils.createPayloadWithParams("QUESTION", payload, "1")),
				new InlineKeyboardButton(no, PayloadUtils.createPayloadWithParams("QUESTION", payload, "0")));
		this.sendButtons(buttonListMarkup, text, message);
	}

	public void gameInterface(List<User> users, Card blackCard, Message message) {
		users.forEach(u -> {
//			if (u.getBlackCard() == null) {

				String payload = PayloadUtils
						.createPayloadWithParams(
								"WHITE_CARD_CHOICE", message.getChat().getId().toString(), u.getChatId().toString());

				message.getChat().setId(u.getChatId());

				sendButtons(createButtonListMarkup(true,
						u.getCards().stream().map(c -> new InlineKeyboardButton(c.getName(), payload))
								.toArray(InlineKeyboardButton[]::new)), blackCard.getName(), message);
//			}
		});
	}
}
