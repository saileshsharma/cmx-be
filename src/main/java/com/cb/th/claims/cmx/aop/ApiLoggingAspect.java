package com.cb.th.claims.cmx.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.cb.th.claims.cmx.util.LogManager.apiLog;

@Aspect
@Component
public class ApiLoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {
    }

    @Pointcut("within(@org.springframework.graphql.data.method.annotation.SchemaMapping *) || within(@org.springframework.graphql.data.method.annotation.QueryMapping *)")
    public void graphqlResolvers() {
    }

    @Before("controllerMethods() || graphqlResolvers()")
    public void logRequest(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        apiLog.info("API CALL → {} with args {}", method, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "controllerMethods() || graphqlResolvers()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();
        apiLog.info("API RESPONSE ← {} returned {}", method, result);
    }
}
