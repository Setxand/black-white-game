package telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CallBackQuery {
	private Long id;
	private UserDTO from;
	private Message message;
	@JsonProperty("chat_instance")
	private Long chatInstance;
	private String data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserDTO getFrom() {
		return from;
	}

	public void setFrom(UserDTO from) {
		this.from = from;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Long getChatInstance() {
		return chatInstance;
	}

	public void setChatInstance(Long chatInstance) {
		this.chatInstance = chatInstance;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
