package com.alinesno.infra.smart.brain.inference.llm.chatgpt;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.brain.api.dto.RequestData;
import com.alinesno.infra.smart.brain.api.reponse.ChatResponse;
import com.alinesno.infra.smart.brain.client.ChatGPTOpenAiStreamClient;
import com.alinesno.infra.smart.brain.service.IChatgptApiService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;

/**
 * chat接口
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class ChatgptApiServiceImpl implements IChatgptApiService {
	
	private static final Logger log = LoggerFactory.getLogger(ChatgptApiServiceImpl.class) ; 

	public String api_reverse_proxy = "";

	public int timeout_ms = 100000;

	@Value("${alinesno.infra.smart.brain.openapi.key:}")
	private String apiKey;

	/**
	 * 自定义api host使用builder的方式构造client
	 */
	@Value("${alinesno.infra.smart.brain.openapi.host}")
	private String apiHost ;

	public static ChatGPTOpenAiStreamClient client = null;

	@Override
	public ChatGPTOpenAiStreamClient getClient() {

		if (client != null) {
			return client;
		}

		// 推荐这种构造方式
		client = ChatGPTOpenAiStreamClient
				.builder()
				.connectTimeout(60)
				.readTimeout(60*2*1000)
				.writeTimeout(60*2*1000)
				.apiKey(apiKey)
				.apiHost(apiHost)
				.build();

		return client;
	}
	
	@Override
	public ChatResponse session() {
		ChatResponse r = ChatResponse.success();
		JSONObject data = new JSONObject();

		data.put("auth", true);
		data.put("model", currentModel());

		r.setData(data);
		return r;
	}

	/**
	 * 当前模式
	 * 
	 * @return
	 */
	private String currentModel() {
		return "ChatGPTAPI";
	}

	@Override
	public ChatResponse chat() {
		return null;
	}

	@Override
	public ChatResponse config() {

//		CreditGrantsResponse balance = fetchBalance();
		String reverseProxy = api_reverse_proxy;
		String apiModel = this.currentModel();

		JSONObject json = new JSONObject();

		json.put("apiModel", apiModel);
		json.put("reverseProxy", reverseProxy);
		json.put("timeoutMs", timeout_ms);
		json.put("httpsProxy", "-");

		ChatResponse r = ChatResponse.success() ;
		r.setData(json);

		return r;
	}

	@Override
	public ChatResponse chatProcess() {
		return ChatResponse.success();
	}

	@Deprecated
	@Override
	public ChatResponse verify(String email , String token) {
		String msg = "Verify successfully";

		if (StringUtils.isBlank(token)) {
			throw new RpcServiceRuntimeException("Secret key is empty");
		}
	
		// 测试授权码
		if(token.equals("admin")) {
			return ChatResponse.successMsg(msg);
		}

		return ChatResponse.successMsg(msg);
	}

	@Override
	public ChatResponse chatCompletions(RequestData data) {
		return null;
	}

}
