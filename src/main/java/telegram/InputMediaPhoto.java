package telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InputMediaPhoto implements InputMedia {
	private String type;
	private String media;
	private String caption;
	@JsonProperty("parse_mode")
	private String parseMode;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getParseMode() {
		return parseMode;
	}

	public void setParseMode(String parseMode) {
		this.parseMode = parseMode;
	}
}
