package com.alinesno.infra.smart.brain.interp.utils;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class OpenAIMessageConverter {
    public static List<JsonObject> convertToOpenAIMessages(List<JsonObject> messages, boolean functionCalling) {
        List<JsonObject> newMessages = new ArrayList<>();

        for (JsonObject message : messages) {
            JsonObject newMessage = new JsonObject();
            newMessage.addProperty("role", message.get("role").getAsString());
            newMessage.addProperty("content", "");

            if (message.has("message")) {
                newMessage.addProperty("content", message.get("message").getAsString());
            }

            if (message.has("code")) {
                if (functionCalling) {
                    JsonObject functionCall = new JsonObject();
                    functionCall.addProperty("name", "run_code");
                    JsonObject arguments = new JsonObject();
                    arguments.addProperty("language", message.get("language").getAsString());
                    arguments.addProperty("code", message.get("code").getAsString());
                    functionCall.add("arguments", arguments);
                    newMessage.add("function_call", functionCall);
                    JsonObject parsedArguments = new JsonObject();
                    parsedArguments.addProperty("language", message.get("language").getAsString());
                    parsedArguments.addProperty("code", message.get("code").getAsString());
                    newMessage.add("parsed_arguments", parsedArguments);
                } else {
                    String content = String.format("\n\n```%s\n%s\n```", message.get("language").getAsString(), message.get("code").getAsString());
                    newMessage.addProperty("content", content.trim());
                }
            }

            newMessages.add(newMessage);

            if (message.has("output")) {
                if (functionCalling) {
                    JsonObject outputMessage = new JsonObject();
                    outputMessage.addProperty("role", "function");
                    outputMessage.addProperty("name", "run_code");
                    outputMessage.addProperty("content", message.get("output").getAsString());
                    newMessages.add(outputMessage);
                } else {
                    JsonObject outputMessage = new JsonObject();
                    outputMessage.addProperty("role", "user");
                    outputMessage.addProperty("content", "CODE EXECUTED ON USER'S MACHINE. OUTPUT (invisible to the user): " + message.get("output").getAsString());
                    newMessages.add(outputMessage);
                }
            }
        }

        return newMessages;
    }

//    public static void main(String[] args) {
//        // Example usage
//        Gson gson = new Gson();
//
//        List<JsonObject> messages = new ArrayList<>();
//        // Add messages to the list...
//
//        boolean functionCalling = true;
//        List<JsonObject> convertedMessages = convertToOpenAIMessages(messages, functionCalling);
//
//        String json = gson.toJson(convertedMessages);
//        System.out.println(json);
//    }
}
