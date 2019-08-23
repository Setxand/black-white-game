package telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplyKeyboardRemove implements Markup {
	@JsonProperty("remove_keyboard")
	private Boolean removeKeyboard;

	public ReplyKeyboardRemove(Boolean removeKeyboard) {
		this.removeKeyboard = removeKeyboard;
	}
}
