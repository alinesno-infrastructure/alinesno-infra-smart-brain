package com.alinesno.infra.smart.brain.api.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TestGptApi {

    @Test
    public void testChat() {
        try {
            URL url = new URL("http://localhost:30302/api/chat-process");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String requestBody = "{\n" +
                    "    \"prompt\":\"帮我发送个配置\",\n" +
                    "    \"options\":{\n" +
                    "        \"conversationId\":\"\",\n" +
                    "        \"parentMessageId\":\"\"\n" +
                    "    },\n" +
                    "    \"systemMessage\":\"You are ChatGPT, a large language model trained by OpenAI. Follow the user's instructions carefully. Respond using markdown.\",\n" +
                    "    \"temperature\":0.8,\n" +
                    "    \"tokens\":1000\n" +
                    "}";

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                String textData = "" ;

                while ((line = reader.readLine()) != null) {
                    JSONObject json = JSONObject.parseObject(line) ;

                    textData = json.getString("text") ;

                    System.out.println(">>>>>>> line = " + textData);

                    response.append(line);
                }
                reader.close();
//                System.out.println(response.toString());

                System.out.println("---->>>>>>>>>>>>>>>>>>>");
                System.out.println("textData = " + textData);

//                List<JSONObject> messages = JSON.parseArray(response.toString(), JSONObject.class);
//                for (JSONObject message : messages) {
//                    System.out.println(message.toJSONString());
//                }

            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
