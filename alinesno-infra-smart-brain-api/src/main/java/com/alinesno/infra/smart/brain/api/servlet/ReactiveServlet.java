package com.alinesno.infra.smart.brain.api.servlet;


import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.brain.api.dto.ChatRequestDto;
import com.alinesno.infra.smart.brain.api.reponse.ChatResponseDto;
import com.alinesno.infra.smart.brain.service.IChatgptApiService;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * GPT响应配置
 * @author luoxiaodong
 * @version 1.0.0
 */
@WebServlet(urlPatterns = "/api/chat-process", asyncSupported = true )
public class ReactiveServlet extends HttpServlet {
	
	private static final Logger log = LoggerFactory.getLogger(ReactiveServlet.class);

	@Autowired
	private IChatgptApiService chatgptApiService;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
		
		resp.setContentType("text/event-stream");
		resp.setCharacterEncoding("UTF-8");
		
		AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(120*1000) ;
		
	    PrintWriter out = asyncContext.getResponse().getWriter();
	    
	    // 正常返回数据
	    generatorOpenApi(asyncContext , out , request) ; 
	}

	private void generatorOpenApi(AsyncContext asyncContext, PrintWriter out, HttpServletRequest request) throws IOException {
		
		InputStream inputStream = request.getInputStream();
		String requestBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		ChatRequestDto chatRequestDto = JSONObject.parseObject(requestBody, ChatRequestDto.class) ;
		
		Flux<Object> flux = Flux.create(emitter -> {
			
			Message message = Message
					.builder()
					.role(Message.Role.ASSISTANT)
					.content(chatRequestDto.getPrompt())
					.build();

			ChatCompletion chatCompletion = ChatCompletion.builder().messages(List.of(message)).build();
			chatgptApiService.getClient().streamChatCompletion(chatCompletion, new EventSourceListener() {
			
				String contentText = "" ; 
				
				@Override
				public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {

					log.debug("data = {}" , data);

					String dataEsc = StringEscapeUtils.unescapeJava(data);
					JSONObject dataEscJSon = JSONObject.parseObject(dataEsc)  ; 
					log.info("OpenAI返回数据：{}", data);
					
					log.debug("data.equals(\"[DONE]\" = {}" , data.contains("[DONE]"));
					
					if (data.equals("[DONE]")) {
						stopProcess(emitter , asyncContext) ; 
						return;
					}
					
					String text = JSONObject.parseObject(data).getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content") ; 
					String finish_reason = JSONObject.parseObject(data).getJSONArray("choices").getJSONObject(0).getString("finish_reason") ; 
				
					if(("stop".equals(finish_reason) || "length".equals(finish_reason))) {
						stopProcess(emitter , asyncContext) ; 
						return;
					}
					
					if(text != null) {
						contentText += text ;
					}
					
					ChatResponseDto dto = new ChatResponseDto() ;
					dto.setId(dataEscJSon.getString("id"));
					dto.setRole(Message.Role.ASSISTANT.getName());
					dto.setParentMessageId(chatRequestDto.getOptions().getParentMessageId()); 
					dto.setText(contentText) ; 
					
					JSONObject jsonData = JSONObject.parseObject(dto.toString()) ; 
					jsonData.put("detail", dataEscJSon) ; 
					
					log.debug("dataEscJSon:{}" , dataEscJSon) ;
					
					emitter.next(jsonData) ; 
				}
			});

		});
		
		
		flux.subscribe(s -> {
			if(s != null && StringUtils.isNotBlank(s.toString())) {
		      out.println(s);
		      out.flush();
			}
		}, Throwable::printStackTrace, asyncContext::complete);

	}

	/**
	 * 停止会话
	 */
	private void stopProcess(FluxSink<Object> emitter  , AsyncContext asyncContext) {
		log.info("OpenAI返回数据结束了");
		emitter.complete(); 
		asyncContext.complete();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}