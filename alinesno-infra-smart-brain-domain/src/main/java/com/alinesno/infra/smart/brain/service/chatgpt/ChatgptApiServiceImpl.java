package com.alinesno.infra.smart.brain.service.chatgpt;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.brain.api.reponse.ChatResponse;
import com.alinesno.infra.smart.brain.client.ChatGPTOpenAiStreamClient;
import com.alinesno.infra.smart.brain.service.IChatgptApiService;
import com.unfbx.chatgpt.entity.billing.CreditGrantsResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	public String openai_api_key = "sk-";

	public String openai_access_token = "";

	public String openai_api_base_url = "";

	public String openai_api_model = "";

	public String api_reverse_proxy = "";

	public int timeout_ms = 100000;

	public int max_request_per_hour = 0;

	public String auth_secret_key = "xVC1UMaEuf20cEWw";

	public String socks_proxy_host = "47.254.64.213";

	public int socks_proxy_port = 24653;

	public String https_proxy = "";

	public static ChatGPTOpenAiStreamClient client = null;

	@Override
	public ChatGPTOpenAiStreamClient getClient() {

		if (client != null) {
			return client;
		}
		
		// 配置代理 
//		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socks_proxy_host, socks_proxy_port));

		// 推荐这种构造方式
		client = ChatGPTOpenAiStreamClient
				.builder()
				.connectTimeout(60)
				.readTimeout(60*2*1000)
				.writeTimeout(60*2*1000)
				.apiKey(openai_api_key)
//				.proxy(proxy)
				.apiHost("https://api.openai.com/")
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

		CreditGrantsResponse balance = fetchBalance();
		String reverseProxy = api_reverse_proxy;
		String socksProxy = socks_proxy_host + ":" + socks_proxy_port;
		String apiModel = this.currentModel();

		JSONObject json = new JSONObject();

		json.put("apiModel", apiModel);
		json.put("reverseProxy", reverseProxy);
		json.put("timeoutMs", timeout_ms);
		json.put("socksProxy", socksProxy);
		json.put("httpsProxy", "-");
		
		json.put("balanceAvailable", balance.getTotalAvailable());
		json.put("balanceUsed", balance.getTotalUsed());
		json.put("balanceTotal", balance.getTotalGranted());
		
		json.put("balance", balance.getTotalAvailable());
		
		ChatResponse r = ChatResponse.success() ; 
		r.setData(json);

		return r;
	}

	private CreditGrantsResponse fetchBalance() {
	
		CreditGrantsResponse creditGrantsResponse = this.getClient().creditGrants() ; 
		
		log.info("账户总余额（美元）：{}", creditGrantsResponse.getTotalGranted());
        log.info("账户总使用金额（美元）：{}", creditGrantsResponse.getTotalUsed());
        log.info("账户总剩余金额（美元）：{}", creditGrantsResponse.getTotalAvailable());
        
		return creditGrantsResponse;
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

		if (!auth_secret_key.equals(token)) {
			throw new RpcServiceRuntimeException("密钥无效 | Secret key is invalid");
		}

		return ChatResponse.successMsg(msg);
	}

}
