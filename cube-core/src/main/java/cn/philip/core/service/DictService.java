package cn.philip.core.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 字典服务
 * @author: pfliu
 * @time: 2020/6/16
 */
@Slf4j
@Service
public class DictService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DS("master")
    public List<Map<String, Object>> getDictItems(String appId, String dictCode, List<String> valueList) {
        String sql = "select * from config_dict where dict_code = '" + dictCode + "' and app_id = '" + appId + "'";
        List<Map<String, Object>> dictList = jdbcTemplate.queryForList(sql);
        if (!dictList.isEmpty()) {
            String type = StrUtil.toString(dictList.get(0).get("dict_type"));
            String dbName = StrUtil.toString(dictList.get(0).get("data_source"));
            String tableName = StrUtil.toString(dictList.get(0).get("table_name"));
            String key = StrUtil.toString(dictList.get(0).get("key"));
            String value = StrUtil.toString(dictList.get(0).get("value"));
            String assKey = StrUtil.toString(dictList.get(0).get("assistant_key"));
            String assValue = StrUtil.toString(dictList.get(0).get("assistant_value"));
            String condition = StrUtil.toString(dictList.get(0).get("condition"));
            String sortNum = StrUtil.toString(dictList.get(0).get("field_sort_num"));
            if ("constant".equalsIgnoreCase(type)) {
                return getConstantDict(dbName, tableName, key, value, assKey, assValue, condition, sortNum, valueList);
            }
        }
        return null;
    }

    /**
     * 查询常量字典
     *
     * @param dbName    数据库
     * @param tableName 表名
     * @param key       key字段
     * @param value     value字段
     * @param assKey    过滤了字段
     * @param assValue  过滤条件
     * @param condition 附加过滤条件
     * @param sortNum   排序字段
     * @param valueList 值列表
     */
    private List<Map<String, Object>> getConstantDict(String dbName, String tableName, String key, String value,
                                                      String assKey, String assValue, String condition, String sortNum, List<String> valueList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select `").append(key).append("` as `key`, `").append(value).append("` as `value` from `").append(tableName)
                .append("` where `").append(assKey).append("` = '").append(assValue).append("'");
        if (StrUtil.isNotBlank(condition) && !"null".equalsIgnoreCase(condition)) {
            sqlBuilder.append(" and ").append(condition);
        }
        if(null != valueList && valueList.size() > 0){
            sqlBuilder.append(" and `").append(key).append("` in (").append(getInCondition(valueList)).append(")");
        }
        if(StrUtil.isNotBlank(sortNum) && !"null".equalsIgnoreCase(sortNum)){
            sqlBuilder.append(" order by ").append(sortNum);
        }
        DynamicDataSourceContextHolder.push(dbName);
        return jdbcTemplate.queryForList(sqlBuilder.toString());
    }

    /**
     * 拼装In条件
     *
     * @param valueList 数据列表
     */
    private String getInCondition(List<String> valueList) {
        StringBuilder resultStr = new StringBuilder();
        if (null != valueList && !valueList.isEmpty()) {
            for (String value : valueList) {
                resultStr.append("'").append(value).append("',");
            }
        }
        return resultStr.toString().substring(0, resultStr.length() - 1);
    }
}
