package com.alinesno.infra.smart.assistant.config;

import com.alinesno.infra.common.facade.response.AjaxResult;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class AjaxResultEventStreamConverter extends AbstractHttpMessageConverter<AjaxResult> {

    public AjaxResultEventStreamConverter() {
        super(MediaType.TEXT_EVENT_STREAM);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return AjaxResult.class.isAssignableFrom(clazz);
    }

    @Override
    protected AjaxResult readInternal(Class<? extends AjaxResult> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected void writeInternal(AjaxResult ajaxResult, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    }
}