package cn.philip.core.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 规则校验服务
 * @author: pfliu
 * @time: 2020/5/22
 */
@Slf4j
@Service
public class RuleService {

    @Autowired
    private ConfigService configService;

    /**
     * 查询值重复的字段
     *
     * @param dbName   数据源
     * @param appCode  应用编码
     * @param funcCode 功能编码
     * @param data     数据
     * @return 重复项
     */
    @DS("#dbName")
    public String uniqueCheck(String dbName, String appCode, String funcCode, Map<String, Object> data) {
        String repeatCode = "";
        List<Map<String, Object>> formField = configService.getViewField(appCode, funcCode);
        for (Map<String, Object> field : formField) {
            String isUnique = StrUtil.toString(field.get("is_unique"));
            if ("1".equals(isUnique)) {
                String fieldCode = StrUtil.toString(field.get("field_code"));
                Object value = data.get(fieldCode);
                if (null != value) {

                }
            }
        }
        return repeatCode;
    }

}
