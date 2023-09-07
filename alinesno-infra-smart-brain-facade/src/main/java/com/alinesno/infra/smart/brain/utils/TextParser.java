package com.alinesno.infra.smart.brain.utils;

/**
 * 生成返回异常信息
 * @author luoandong
 * @version 1.0.0
 */
public class TextParser {

	/**
	 * 生成对话返回信息
	 * @param payMessageInfo
	 * @return
	 */
	public static String parse(String payMessageInfo) {

		String text = "{\n"
				+ "    \"role\": \"assistant\",\n"
				+ "    \"message\": \""+payMessageInfo+"\",\n"
				+ "    \"id\": \"chatcmpl-7eQ1zlNAuvw7U1LxtlTC3e8BXT00G\",\n"
				+ "    \"detail\": {\n"
				+ "        \"created\": 1689867539,\n"
				+ "        \"model\": \"gpt-3.5-turbo-0613\",\n"
				+ "        \"id\": \"chatcmpl-7eQ1zlNAuvw7U1LxtlTC3e8BXT00G\",\n"
				+ "        \"choices\": [\n"
				+ "            {\n"
				+ "                \"delta\": {\n"
				+ "                    \"content\": \"家\"\n"
				+ "                },\n"
				+ "                \"index\": 0\n"
				+ "            }\n"
				+ "        ],\n"
				+ "        \"object\": \"chat.completion.chunk\"\n"
				+ "    }\n"
				+ "}" ;

		return text ;
	}

}
