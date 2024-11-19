package com.alinesno.infra.smart.assistant.api.validate;

import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import javax.lang.exception.RpcServiceRuntimeException;
import java.lang.reflect.Type;
import java.util.Map;

public class ConditionalValidJsonValidator implements ConstraintValidator<ConditionalValidJson, ToolRequestDto> {

    @Override
    public boolean isValid(ToolRequestDto toolDto, ConstraintValidatorContext context) {
        if ("save".equals(toolDto.getParams())) {
            return true; // 当type为"save"时，不校验params
        }

        String params = toolDto.getParams();
        if (params == null || params.isEmpty()) {
            return true; // 空字符串或null不进行校验
        }

        try {
            // 尝试将JSON对象转换为Map
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();

            gson.fromJson(params, type);
            return true; // 成功转换为Map
        } catch (Exception e) {
            throw new RpcServiceRuntimeException("输入参数格式错误，请输入JSON格式: " + params);
        }
    }
}