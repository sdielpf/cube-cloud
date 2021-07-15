package cn.philip.core.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import cn.philip.common.exception.CubeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 配置信息服务类
 * @author: pfliu
 * @time: 2020/5/20
 */
@Slf4j
@Service
@DS("master")
public class ConfigService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询数据源
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     * @return 数据源名称
     */
    public String getDataSourceName(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getDataSource", replaceMap);
        log.debug(querySql);
        try {
            return jdbcTemplate.queryForObject(querySql, String.class);
        } catch (Exception e) {
            throw new CubeException(500, "数据源配置有误，请检查");
        }
    }

    /**
     * 查询模型字段配置
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    public List<String> getModelField(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getModelField", replaceMap);
        log.debug(querySql);
        List<Map<String, Object>> fieldList = jdbcTemplate.queryForList(querySql);
        List<String> columns = new ArrayList<>();
        for (Map<String, Object> field : fieldList) {
            columns.add(StrUtil.toString(field.get("field_code")));
        }
        return columns;
    }

    /**
     * 查询主键
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    public String getPrimaryKey(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getPrimaryKey", replaceMap);
        log.debug(querySql);
        return jdbcTemplate.queryForObject(querySql, String.class);
    }

    /**
     * 查询表名
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    public String getTableName(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getTableName", replaceMap);
        log.debug(querySql);
        Map<String, Object> dataInfo = jdbcTemplate.queryForMap(querySql);
        String state = StrUtil.toString(dataInfo.get("state"));
        if (dataInfo.isEmpty()) {
            throw new CubeException(500, "此功能未绑定模型");
        }
        if (!"1".equals(state)) {
            throw new CubeException(500, "该功能不允许操作");
        }
        return StrUtil.toString(dataInfo.get("model_code"));
    }

    /**
     * 查询数据集
     *
     * @param collectionCode 数据集编码
     * @param replaceMap     可替换
     * @return 数据集
     */
    public String getCollection(String collectionCode, Map<String, Object> replaceMap) {
        String querySql = "";
        List<Map<String, Object>> collectionList = jdbcTemplate.queryForList("select * from config_collection where collection_code = '" + collectionCode + "'");
        if (collectionList.size() > 0) {
            querySql = StrUtil.toString(collectionList.get(0).get("query_raw"));
        }
        if (null != replaceMap) {
            for (String key : replaceMap.keySet()) {
                querySql = querySql.replace("#{" + key + "}", StrUtil.toString(replaceMap.get(key)));
            }
        }
        return querySql;
    }

    /**
     * 获取数据机
     *
     * @param tableName 表名（模型编码）
     */
    public String getCollection(String tableName) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("modelCode", tableName);
        String querySql = getCollection("getTableSql", replaceMap);
        return jdbcTemplate.queryForObject(querySql, String.class);
    }

    /**
     * 查询过滤器
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    public List<Map<String, Object>> getFilter(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getFilter", replaceMap);
        log.debug("get {} - {} filter\n final sql: {}", appCode, funcCode, querySql);
        return jdbcTemplate.queryForList(querySql);
    }

    /**
     * 查询表单字段
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    public List<Map<String, Object>> getViewField(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getViewField", replaceMap);
        log.debug("get {} - {} field\n final sql: {}", appCode, funcCode, querySql);
        return jdbcTemplate.queryForList(querySql);
    }

    /**
     * 查询表单字段
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    public List<Map<String, Object>> getButtons(String appCode, String funcCode) {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("appCode", appCode);
        replaceMap.put("funcCode", funcCode);
        String querySql = getCollection("getButton", replaceMap);
        log.debug("get {} - {} button\n final sql: {}", appCode, funcCode, querySql);
        return jdbcTemplate.queryForList(querySql);
    }

    /**
     * 查询应用信息
     *
     * @param appCode 应用编码
     */
    public Map<String, Object> getAppInfo(String appCode) {
        String sql = "select * from config_app where app_code = '" + appCode + "'";
        List<Map<String, Object>> appList = jdbcTemplate.queryForList(sql);
        if (!appList.isEmpty()) {
            return appList.get(0);
        }
        return null;
    }

}
