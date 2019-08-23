package telegram.button;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InlineKeyboardButton implements Button {
	private String text;
	private String url;
	@JsonProperty("callback_data")
	private String callBackData;

	public InlineKeyboardButton(String text, String callBackData) {
		this.text = text;
		this.callBackData = callBackData;
	}

	public InlineKeyboardButton() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCallBackData() {
		return callBackData;
	}

	public void setCallBackData(String callBackData) {
		this.callBackData = callBackData;
	}
}
