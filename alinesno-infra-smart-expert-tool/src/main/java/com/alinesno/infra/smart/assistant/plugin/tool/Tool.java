package com.alinesno.infra.smart.assistant.plugin.tool;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public abstract class Tool {

   /**
    *  账号ID
    */
   public String accountId ;

   /**
    *  组织ID
    */
   public String accountOrgId ;

   /**
    * 知识库ID
    */
   public String datasetIds ;

   /**
   * 任务执行
   * @return
   */
   public abstract String execute() ;

   /**
    * 任务执行
    * @return
    */
   public void initParams(){
      // 初始化参数
   }

   /**
    * 密钥配置
    */
   public Map<String, String> secretKey ;

   /**
    * 工具执行结束后，是否结束本轮对话
    * @return
    */
   public boolean isFinished(){
      return false ;
   }

   /**
    * 返回参数的格式
    * @return
    */
   public String toJson() {
      ToolInfo toolInfo = getClass().getAnnotation(ToolInfo.class);
      String toolName = toolInfo.name().isEmpty() ? getClass().getSimpleName() : toolInfo.name();

      JSONObject toolJson = new JSONObject();

      toolJson.put("id", IdUtil.nanoId(8));
      toolJson.put("name", toolName);
      toolJson.put("type", "stdio");  // 本地工具
      toolJson.put("description", toolInfo.description());

      List<JSONObject> paramDescriptions = new ArrayList<>();
      Field[] fields = getClass().getDeclaredFields();
      for (Field field : fields) {
         ParamInfo paramInfo = field.getAnnotation(ParamInfo.class);
         if (paramInfo != null) {
            JSONObject paramJson = new JSONObject();
            paramJson.put("name", paramInfo.name().isEmpty() ? field.getName() : paramInfo.name());
            paramJson.put("description", paramInfo.description());
            paramJson.put("type", paramInfo.type().isEmpty() ? field.getType().getSimpleName() : paramInfo.type());
            paramDescriptions.add(paramJson);
         }
      }

      toolJson.put("paramDescription", paramDescriptions);
      return JSON.toJSONString(toolJson);
   }

   public String toToolName() {
      ToolInfo toolInfo = getClass().getAnnotation(ToolInfo.class);
       return toolInfo.name().isEmpty() ? getClass().getSimpleName() : toolInfo.name();
   }


   /**
    * 获取到包括包路径一起的全称
    * @return
    */
   public String getFullName() {
      return getClass().getName() ;
   }
}
