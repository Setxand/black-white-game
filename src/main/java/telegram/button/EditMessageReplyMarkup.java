package telegram.button;

import com.fasterxml.jackson.annotation.JsonProperty;
import telegram.Markup;

public class EditMessageReplyMarkup implements Markup {

	@JsonProperty("chat_id")
	public Integer chatId;
	@JsonProperty("message_id")
	public Integer messageId;
	@JsonProperty("reply_markup")
	public Markup markup;

}
