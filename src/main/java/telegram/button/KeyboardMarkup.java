package telegram.button;

import telegram.Markup;

import java.util.List;

public class KeyboardMarkup implements Markup {
	private List<List<Button>> keyboard;

	public KeyboardMarkup(List<List<Button>> keyboard) {
		this.keyboard = keyboard;
	}

	public KeyboardMarkup() {
	}

	public List<List<Button>> getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(List<List<Button>> keyboard) {
		this.keyboard = keyboard;
	}
}

