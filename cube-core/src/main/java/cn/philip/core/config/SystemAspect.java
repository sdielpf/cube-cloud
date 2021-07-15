package cn.philip.core.config;

import cn.philip.common.exception.CubeException;
import cn.philip.core.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description: 切面
 * @author: pfliu
 * @time: 2020/5/28
 */
@Slf4j
@Aspect
@Component
public class SystemAspect {

    @Autowired
    private ConfigService configService;

    @Pointcut("@within(cn.philip.core.config.CheckApp)")
    public void appAspect() {

    }

    @Around("appAspect()")
    public Object appAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Optional.ofNullable(servletRequestAttributes).map(ServletRequestAttributes::getRequest).orElse(null);
        String uri = Objects.requireNonNull(request).getRequestURI();
        String appCode = uri.split("/")[1];
        // 查询应用是否存在
        Map<String, Object> appInfo = configService.getAppInfo(appCode);
        if (null == appInfo) {
            throw new CubeException(500, appCode + "'s not support");
        }
        //TODO：判断用户是否有访问这个应用的权限
        return joinPoint.proceed();
    }


}
