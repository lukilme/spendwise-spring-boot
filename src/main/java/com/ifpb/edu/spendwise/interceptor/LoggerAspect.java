package com.ifpb.edu.spendwise.interceptor;

import java.util.Iterator;

import org.apache.catalina.session.StandardSessionFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.ifpb.edu.spendwise.util.LoggerHandle;

@Aspect
@Component
public class LoggerAspect {
    
    @Before("@annotation(loggerAnnotation)")
    public void logBefore(JoinPoint joinPoint, Logger loggerAnnotation) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("LOG: Executando método: " + methodName);
    }

    
    @Before("execution(@org.springframework.web.bind.annotation.GetMapping * *(..))")
    public void logBeforeGetMapping(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        try {
            for (Object object : joinPoint.getArgs()) {
                if(object!=null){
                    LoggerHandle.info(object.toString());
                    if (object.toString().matches("org.apache.catalina.session.StandardSessionFacade@.*")) {
                    StandardSessionFacade sup = (StandardSessionFacade) object;
                    Iterator<String> iterator = sup.getAttributeNames().asIterator();
                    int index = 0;
                    LoggerHandle.info("Interando a váriavel -> " + sup.getClass().toString());
                    while (iterator.hasNext()) {

                        String thing = iterator.next();
                        LoggerHandle.infoIterator(index, thing);
                        index++;
                    }
                } else {
                    LoggerHandle.info(object.toString());
                }
                }else{
                    LoggerHandle.info("Object Null");
                }

                
            }
            LoggerHandle.warning("Método com @GetMapping executado: " + methodName);
        } catch (Exception e) {
            LoggerHandle.erro(e);
        }

        // Iterator<String> iterator = session.getAttributeNames().asIterator();
        // while (iterator.hasNext()) {
        // String thing = iterator.next();
        // System.out.println(thing);

        // }

    }

    @Before("execution(@org.springframework.web.bind.annotation.PostMapping * *(..))")
    public void logBeforePostMapping(JoinPoint joinPoint) {
        System.out.println("LOG Before: Método com @PostMapping executado: ");
        String methodName = joinPoint.getSignature().getName();
        for (Object object : joinPoint.getArgs()) {
            LoggerHandle.info(object.toString());
        }
        LoggerHandle.warning("LOG: Método com @PostMapping executado: " + methodName);
    }

    @After("execution(@org.springframework.web.bind.annotation.PostMapping * *(..))")
    public void logAfterPostMapping(JoinPoint joinPoint) {
         System.out.println("LOG After: Método com @PostMapping executado: ");
        String methodName = joinPoint.getSignature().getName();
        for (Object object : joinPoint.getArgs()) {
            LoggerHandle.info(object.toString());
        }
        LoggerHandle.warning("LOG: Método com @PostMapping executado: " + methodName);
    }

}
