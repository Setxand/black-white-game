package telegram.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import telegram.Markup;
import telegram.Message;
import telegram.ReplyKeyboardRemove;
import telegram.TelegramRequest;
import telegram.button.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class TelegramClient {
	protected final String SERVER_URL;
	private final String WEBHOOK;
	private final Map<String, String> urlMap;
	private RestTemplate restTemplate;

	public TelegramClient(String serverUrl, String webhook, String urls) {
		SERVER_URL = serverUrl;
		WEBHOOK = webhook;

		restTemplate = new RestTemplate();
//		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//		converter.setSupportedMediaTypes(
//				Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM,
//						MediaType.MULTIPART_FORM_DATA));
//		restTemplate.setMessageConverters(Arrays.asList(converter, new FormHttpMessageConverter()));

		urlMap = processMap(urls);
	}

	/**
	 * @Example Webhooks in application.properties:
	 * telegram.webhooks=/example1,/example2
	 */
	public void setWebHooks() {
		String[] webhooks = WEBHOOK.split(",");
		for (int i = 0; i < webhooks.length; i++) {
			String webhook = webhooks[i];
			List<String> strings = new ArrayList<>(urlMap.values());
			setWebHook(webhook, strings.get(i));
		}
	}

	public void helloMessage(Message message) {
		String helloMessage = ResourceBundle.getBundle("dictionary").getString("HELLO_MESSAGE");
		int chatId = message.getChat().getId();
		sendMessage(new TelegramRequest(helloMessage, chatId, message.getPlatform()));
	}

	public void simpleMessage(String message, Message m) {
		sendMessage(new TelegramRequest(message, m.getChat().getId(), m.getPlatform()));
	}

	public void errorMessage(Message message) {
		String text = "men, i don`t understand this command, try again)";
		sendMessage(new TelegramRequest(text, message.getChat().getId(), message.getPlatform()));
	}

	public void sendButtons(Markup markup, String text, Message message) {
		TelegramRequest telegramRequest = new TelegramRequest();
		telegramRequest.setChatId(message.getChat().getId());
		telegramRequest.setText(text);
		telegramRequest.setMarkup(markup);
		telegramRequest.setPlatform(message.getPlatform());
		sendMessage(telegramRequest);
	}

	public void sendPhoto(String photo, String caption, Markup markup, Message message) {
		restTemplate.postForEntity(urlMap.get(message.getPlatform().name()) + "/sendPhoto",
				new TelegramRequest(message.getChat().getId(), markup, photo, caption), Void.class);
	}

	public void simpleQuestion(String splitter, String text, Message message) {
		String yes = ResourceBundle.getBundle("dictionary").getString("YES");
		String no = ResourceBundle.getBundle("dictionary").getString("NO");
		Markup buttonListMarkup = createButtonListMarkup(true,
				new InlineKeyboardButton(yes, "Yes" + splitter + "QUESTION_YES"),
				new InlineKeyboardButton(no, "No" + splitter + "QUESTION_NO"));
		sendButtons(buttonListMarkup, text, message);
	}

	public void noEnoughPermissions(Message message) {
		String text = "You have not enough permissions to make it!";
		simpleMessage(text, message);
	}

	public void removeKeyboardButtons(Message message) {
		TelegramRequest telegramRequest = new TelegramRequest();
		telegramRequest.setMarkup(new ReplyKeyboardRemove(true));
		telegramRequest.setText("Done");
		telegramRequest.setChatId(message.getChat().getId());
		sendMessage(telegramRequest);
	}

	public void editInlineButtons(Markup markup, Message message) {
		TelegramRequest request = new TelegramRequest();
		request.command = "/editMessageReplyMarkup";
		request.messageId = message.getMessageId();
		request.setChatId(message.getChat().getId());
		request.setMarkup(markup);
		request.setPlatform(message.getPlatform());
		sendMessage(request);
	}

	public void deleteMessage(Message message) {
		TelegramRequest request = new TelegramRequest();
		request.command = "/deleteMessage";
		request.messageId = message.getMessageId();
		request.setChatId(message.getChat().getId());
		request.setPlatform(message.getPlatform());
		sendMessage(request);
	}

	public void sendFile(Message message, FileSystemResource file) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("document", file);
		body.add("chat_id", message.getChat().getId());

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		String serverUrl = urlMap.get(message.getPlatform().name()) + "/sendDocument";

		restTemplate.postForEntity(serverUrl, requestEntity, String.class);
	}

	public byte[] loadDoc(Message message, String path) {
		String url = urlMap.get(message.getPlatform().name());
		StringBuilder stringBuilder = new StringBuilder(url);
		stringBuilder.insert(stringBuilder.indexOf("/bot"), "/file");
		stringBuilder.append("/").append(path);

		return restTemplate.exchange(stringBuilder.toString(),
				HttpMethod.GET,
				null,
				byte[].class).getBody();
	}

	public Map<String, Object> getDocument(String docId, Message message) {
		return restTemplate.exchange(urlMap.get(message.getPlatform().name()) + "/getFile?file_id=" + docId,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Map<String, Object>>() {
				}).getBody();
	}

	private static Map<String, String> processMap(String urls) {
		String[] urlss = urls.split(",");
		Map<String, String> map = new LinkedHashMap<>();
		for (int i = 0; i < urlss.length - 1; i += 2) map.put(urlss[i], urlss[i + 1]);
		return map;
	}

	private static Markup keyBoardOrInline(List<List<Button>> complexButtons) {
		return complexButtons.get(0).get(0) instanceof KeyboardButton ? new KeyboardMarkup(complexButtons) :
				new InlineKeyboardMarkup(complexButtons);
	}

	protected void setWebHook(String webhook, String url) {
		restTemplate.getForEntity(url + "/setWebhook?url=" + SERVER_URL + webhook, Object.class);
	}

	protected void sendMessage(TelegramRequest telegramRequest) {
		try {
			restTemplate.postForEntity(urlMap.get(telegramRequest.getPlatform().name()) + telegramRequest.command,
					telegramRequest, Void.class);
		} catch (HttpClientErrorException ex) {
			throw new RuntimeException("Telegram request error : " + ex.getResponseBodyAsString());
		}
	}

	protected List<List<Button>> createButtonList(boolean horizontal, Button... buttons) {
		List<Button> buttonList = Arrays.asList(buttons);
		return horizontal ? Collections.singletonList(buttonList) :
				buttonList.stream().map(Arrays::asList).collect(Collectors.toList());
	}

	protected Markup createButtonListMarkup(boolean horizontal, Button... buttons) {
		List<List<Button>> complexButtons = createButtonList(horizontal, buttons);
		return keyBoardOrInline(complexButtons);
	}
}
