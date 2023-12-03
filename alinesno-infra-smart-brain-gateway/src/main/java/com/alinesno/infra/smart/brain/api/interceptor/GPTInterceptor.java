package com.alinesno.infra.smart.brain.api.interceptor;

import com.alinesno.infra.smart.brain.service.IAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.annotation.Annotation;

/**
 * GPT 拦截器，用于拦截带有 GPT 注解的方法。
 *
 * <p>作者：luoxiaodong</p>
 *@version 1.0.0
 */
@Component
public class GPTInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(GPTInterceptor.class);

	@Autowired
	private IAccountService accountService;

	/**
	 * 在请求处理之前执行的方法。
	 *
	 * @param request  HttpServletRequest 对象
	 * @param response HttpServletResponse 对象
	 * @param handler  处理器对象
	 * @return 是否继续执行后续的处理器和拦截器
	 * @throws Exception 可能抛出的异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (handler instanceof HandlerMethod) {

			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Annotation annotation = handlerMethod.getMethod().getAnnotation(GPT.class);

			if (annotation != null) {
				String authorizationHeader = request.getHeader("Authorizations");

				if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
					// 如果请求头中没有Authorization参数或参数不符合要求，则返回异常
					throw new RuntimeException("Missing or invalid Authorization header");
				}

				String token = authorizationHeader.substring("Bearer ".length());

				// 在这里你可以使用token进行后续的处理，比如解析、验证等
				log.debug("token = {}", token);

				// 判断是否存在用户
				boolean b = accountService.isHasToken(token);

				return true;
			}

			// 继续执行后续的处理器和拦截器
			return true;
		}
		// 没有拦截到指定注解的情况下默认放行请求
		return true;
	}

	/**
	 * 在请求处理之后，视图渲染之前执行的方法。
	 *
	 * @param request      HttpServletRequest 对象
	 * @param response     HttpServletResponse 对象
	 * @param handler      处理器对象
	 * @param modelAndView ModelAndView 对象
	 * @throws Exception 可能抛出的异常
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {
		// 在请求处理之后，视图渲染之前执行，可以对数据进行处理
	}

	/**
	 * 在整个请求结束之后执行的方法。
	 *
	 * @param request  HttpServletRequest 对象
	 * @param response HttpServletResponse 对象
	 * @param handler  处理器对象
	 * @param ex       异常对象
	 * @throws Exception 可能抛出的异常
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 在整个请求结束之后执行，可以进行一些资源清理操作
	}
}
