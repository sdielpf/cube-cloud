package cn.philip.core.config;

import cn.philip.core.entity.ConfigVariable;
import cn.philip.core.service.VariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 系统启动完成后运行
 * @author: pfliu
 * @time: 2020/8/31
 */
@Slf4j
@Order(1)
@Component
public class ApplicationStartup implements ApplicationRunner {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private VariableService variableService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("core started up, caching variables ...");
        List<ConfigVariable> variableList = variableService.list();
        if (null != variableList && variableList.size() > 0) {
            for (ConfigVariable variable : variableList) {
                redisTemplate.opsForValue().set(variable.getVarCode(), variable);
            }
            log.info("cached {} variables.", variableList.size());
        } else {
            log.warn("no variable cached");
        }


    }
}
