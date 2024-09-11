package com.alinesno.infra.base.im.gateway.utils;

import org.junit.Before;

public class MessageParserTest {

    private String message;

    @Before
    public void setUp() {
        message = "This is a test message @user1 @user2 and #project1.0 #project2.0";
    }


//    public static void main(String[] args) {
//        String message = "This is a test message @user1 @user2 and #1.0 #project2.0";
//
//        MessageParser.ParsedData parsedData = MessageParser.parseMessage(message);
//
//        System.out.println("提到的用户名:");
//        for (String username : parsedData.getUsernames()) {
//            System.out.println(username);
//        }
//
//        System.out.println("项目代号:");
//        for (String projectCode : parsedData.getProjectCodes()) {
//            System.out.println(projectCode);
//        }
//
//        System.out.println("用户名数量: " + parsedData.getUserCount());
//        System.out.println("项目代号数量: " + parsedData.getProjectCount());
//    }

}
