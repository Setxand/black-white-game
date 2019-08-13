package com.blackonwhite.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class PayloadUtils {

	public static String getCommonPayload(String basePayload) {
		return basePayload.contains("?") ? basePayload.substring(0, basePayload.indexOf("?")) : basePayload;
	}

	public static String[] getParams(String basePayload) {
		return basePayload.substring(basePayload.indexOf("?") + 1).split("&");
	}

	public static String createPayloadWithParams(String payload, List<String> params) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(payload).append("?");
		params.forEach(p -> stringBuilder.append(p).append("&"));
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

	public static String createPayloadWithParams(String payload, String... params) {
		return createPayloadWithParams(payload, Arrays.asList(params));
	}

	public static String reformPayloadForQuestion(String payload) {
		String base = getCommonPayload(payload);
		String[] params = getParams(payload);
		List<String> list = new LinkedList<>(Arrays.asList(params));
		list.add(0, base);
		return createPayloadWithParams("QUESTION_PAYLOAD", list.toArray(new String[0]));
	}
}
