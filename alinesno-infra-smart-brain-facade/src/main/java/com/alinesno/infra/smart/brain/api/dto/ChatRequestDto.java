package com.alinesno.infra.smart.brain.api.dto;

import com.alinesno.infra.smart.brain.api.reponse.ChatResponse;
import lombok.Data;

/**
 * DTO（Data Transfer Object）用于封装聊天请求的数据。
 * @author luoxiaodong
 * @version 1.0.0
 */
@Data
public class ChatRequestDto {

	private String prompt;
	private ChatResponse options;
	private String systemMessage;
	private double temperature ;
	private int top_p = 1 ;
	private int tokens ;

}
