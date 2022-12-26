package cn.edu.hhu.a34backend.common.aop;

import cn.edu.hhu.a34backend.utils.HttpContextUtils;
import cn.edu.hhu.a34backend.utils.IpUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志切面
 *
 */


@Aspect
@Component
@Slf4j
public class LogAspect {
    @Value("${setting.debug-level}")
    private int debugLevel;

    @Pointcut("@annotation(cn.edu.hhu.a34backend.common.aop.LogAnnotation)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(point, time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        // debugLevel小于1时不输出Log信息
        if(debugLevel < 0) return;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        log.info("=====================log start================================");
        log.info("module:{}",logAnnotation.module());
        log.info("operation:{}",logAnnotation.operation());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}",className + "." + methodName + "()");

        //大于0时，打印具体请求的参数（可能有明文暴露密码的风险）
        if(debugLevel > 0){
            Object[] args = joinPoint.getArgs();
            String params ="";
            if (!JSON.toJSONString(args).isEmpty()){
                params=JSON.toJSONString(args);
            }
            log.info("params:{}",params);
        }

        //获取request 设置IP地址
        HttpServletRequest request = (HttpServletRequest) HttpContextUtils.getHttpServletRequest();
        log.info("ip:{}", IpUtils.getIpAddr(request));

        log.info("excute time : {} ms",time);
        log.info("=====================log end================================");
    }
}

