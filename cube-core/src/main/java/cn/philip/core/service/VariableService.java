package cn.philip.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.philip.core.entity.ConfigVariable;
import cn.philip.core.mapper.VariableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * @description: 系统变量服务
 * @author: pfliu
 * @time: 2020/9/7
 */
@Slf4j
@Service
public class VariableService extends ServiceImpl<VariableMapper, ConfigVariable> {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询变量值
     *
     * @param variableCode 系统变量变量编码
     */
    public Object getVariableValue(String variableCode) {
        ConfigVariable variable = (ConfigVariable) redisTemplate.opsForValue().get(variableCode);
        if (null != variable) {
            switch (variable.getVarType()) {
                case "1":
                    return variable.getValue();
                case "2":
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * 系统变量自定义驱动执行
     *
     * @param variable 变量信息
     * @return 结果
     * @throws Exception Object
     */
    public static Object execute(ConfigVariable variable, Object... args) throws Exception {
        String url = variable.getValue();
        String className = url.substring(0, url.lastIndexOf("."));
        String methodName = url.substring(url.lastIndexOf(".") + 1);
        try {
            Class c = Class.forName(className);
            Object obj = c.newInstance();
            //调用Class类的方法getMethod获取指定的方法
            Method method = c.getMethod(methodName);
            //调用Method类的方法invoke运行sleep方法
            return method.invoke(obj, args);
        } catch (Exception e) {
            log.error("invoke error", e);
            return "";
        }
    }
}
