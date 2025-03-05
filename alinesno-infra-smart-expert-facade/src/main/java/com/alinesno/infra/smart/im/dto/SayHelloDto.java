package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SayHelloDto类用于封装打招呼的消息数据
 * 它包含了消息的内容、格式化内容、发送者信息以及相关角色和渠道的信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SayHelloDto extends BaseDto {

   /**
    * 消息的内容
    */
   private String greeting;

   /**
    * 格式化后的消息内容，用于特定的展示或处理需求
    */
   private String formatContent;

   /**
    * 发送者的名字
    */
   private String roleName ;

   /**
    * 发送者的角色类型，用于标识发送者在系统中的角色
    */
   private String roleType;

   /**
    * 消息接收者的类型，用于标识接收者在系统中的角色或群体
    */
   private String readerType;

   /**
    * 角色的头像URL或标识，用于展示发送者的头像
    */
   private String roleAvatar;

   /**
    * 消息发送的渠道ID，用于标识消息通过哪个渠道发送
    */
   private String channelId;

   /**
    * 发送者的角色ID，更具体地标识发送者在系统中的角色
    */
   private String roleId;
}
