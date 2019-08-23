package telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document {

	@JsonProperty("file_name")
	public String name;

	@JsonProperty("mime_type")
	public String mimeType;

	@JsonProperty("file_id")
	public String fileId;

	@JsonProperty("file_size")
	public String size;

}
