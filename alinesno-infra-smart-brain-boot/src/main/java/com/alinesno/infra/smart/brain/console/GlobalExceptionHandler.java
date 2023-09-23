package com.alinesno.infra.smart.brain.console;

import java.nio.file.AccessDeniedException;

import com.alinesno.infra.common.web.adapter.exception.BaseException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.HttpStatus;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

import javax.security.auth.login.AccountExpiredException;

/**
 * 全局异常处理器
 * 
 * @author LuoAnDong
 * @version 1.0.0
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 基础异常
	 */
	@ExceptionHandler(BaseException.class)
	public AjaxResult baseException(BaseException e) {
		return AjaxResult.error(e.getMessage());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public AjaxResult handlerNoFoundException(Exception e) {
		log.error(e.getMessage(), e);
		return AjaxResult.error(HttpStatus.NOT_FOUND, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public AjaxResult handleAuthorizationException(AccessDeniedException e) {
		log.error(e.getMessage());
		return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
	}

	@ExceptionHandler(AccountExpiredException.class)
	public AjaxResult handleAccountExpiredException(AccountExpiredException e) {
		log.error(e.getMessage(), e);
		return AjaxResult.error("服务器异常，请稍后重试");
	}

	/**
	 * 自定义验证异常
	 */
	@ExceptionHandler(BindException.class)
	public AjaxResult validatedBindException(BindException e) {
		log.error(e.getMessage(), e);
		String message = e.getAllErrors().get(0).getDefaultMessage();
		return AjaxResult.error(message);
	}

	/**
	 * 自定义验证异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object validExceptionHandler(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);
		String message = e.getBindingResult().getFieldError().getDefaultMessage();
		return AjaxResult.error(message);
	}

	/**
	 * 持久层异常处理
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler({ PersistenceException.class, MybatisPlusException.class })
	public AjaxResult handlePersisException(Exception e) {
		log.error(e.getMessage(), e);
		return AjaxResult.error("持久层错误，请稍后联系管理员");
	}

}