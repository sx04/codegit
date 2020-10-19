package com.cetcbigdata.spider.work;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProcesserAspect {

	private static final Logger LOG = LoggerFactory.getLogger(ProcesserAspect.class);

	ThreadLocal<Long> startTime = new ThreadLocal<>();

	Map<Object, Integer> errorCount = new ConcurrentHashMap();

	@Pointcut("@annotation(com.cetcbigdata.spider.factory.Processer)")
	public void pointCut() {
	}

	@AfterThrowing(value = "pointCut()", throwing = "e")
	public void doAfterThrowing(JoinPoint jp, Throwable e) {
		LOG.error("this process under a error {}", e);
		String tragetClassName = jp.getSignature().getDeclaringTypeName();
		if (errorCount != null) {
			if (errorCount.get(tragetClassName) != null) {
				if (errorCount.get(tragetClassName).intValue() >= 1) {
					Integer count = errorCount.get(tragetClassName).intValue();
					errorCount.put(tragetClassName, ++count);
					LOG.warn("errorcount {}", errorCount.get(tragetClassName).intValue());
				}

			} else {
				errorCount.put(tragetClassName, 1);
			}

		} else {
			errorCount.put(tragetClassName, 1);
		}

	}

	@Before("pointCut()")
	public void beforMehhod(JoinPoint jp) {
		startTime.set(System.currentTimeMillis());
	}

	/**
	 * @param jp
	 */
	@AfterReturning("pointCut()")
	public void afterMehhod(JoinPoint jp) {
		long end = System.currentTimeMillis();
		long total = end - startTime.get();
		String tragetClassName = jp.getSignature().getDeclaringTypeName();
		LOG.info("[runTime] " + tragetClassName + " ,execute over used:" + total + "ms");
	}
}
