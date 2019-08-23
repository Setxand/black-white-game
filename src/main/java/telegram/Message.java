package telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Message {
	public Document document;
	@JsonProperty("message_id")
	private Integer messageId;
	private UserDTO from;
	private Integer date;
	private Chat chat;
	private String text;
	private Platform platform;
	private List<TelegramEntity> entities;
	private List<Photo> photo;

	public Message() {
	}

	public Message(Chat chat) {
		this.chat = chat;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public UserDTO getFrom() {
		return from;
	}

	public void setFrom(UserDTO from) {
		this.from = from;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public List<TelegramEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<TelegramEntity> entities) {
		this.entities = entities;
	}

	public List<Photo> getPhoto() {
		return photo;
	}

	public void setPhoto(List<Photo> photo) {
		this.photo = photo;
	}
}
