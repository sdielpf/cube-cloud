package cn.philip.core.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import cn.philip.common.utils.PkGenerateUtil;
import cn.philip.core.utils.SqlGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 通用数据操作服务
 * @author: pfliu
 * @time: 2020/5/20
 */
@Slf4j
@Service
public class CommonService {

    @Autowired
    private ConfigService configService;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @DS("#dbName")
    public Map<String, Object> saveOrUpdate(String dbName, String appCode, String funcCode, Map<String, Object> data) {
        Assert.notEmpty(data, "Saving data can not be empty.");
        Map<String, Object> result = new HashMap<>(2);
        List<String> columnList = configService.getModelField(appCode, funcCode);
        String tableName = configService.getTableName(appCode, funcCode);
        String primaryKey = configService.getPrimaryKey(appCode, funcCode);
        result.put("primaryKey", primaryKey);
        if (null == data.get(primaryKey)) {
            String pk = PkGenerateUtil.snowFlakeStr();
            result.put("id", pk);
            data.put(primaryKey, pk);
        } else {
            result.put("id", data.get(primaryKey));
        }
        // 剔除data中无用的字段
        Map<String, Object> finalData = new HashMap<>();
        for (String col : columnList) {
            if (null != data.get(col)) {
                finalData.put(col, data.get(col));
            }
        }
        // 需要保存的值
        Object[] objects = new Object[finalData.size()];
        // 需要保存的列
        List<String> saveColumns = new ArrayList<>();
        int size = 0;
        for (String key : finalData.keySet()) {
            saveColumns.add(key);
            objects[size] = finalData.get(key);
            size++;
        }
        String insertSql = SqlGenerateUtil.getInsertSql(tableName, saveColumns);
        log.info("replace action\n params: {}\n final sql: {}", data.toString(), insertSql);
        DynamicDataSourceContextHolder.push(dbName);
        jdbcTemplate.update(insertSql, objects);
        return result;
    }

    /**
     * 修改指定列
     *
     * @param dbName   数据库名
     * @param appCode  应用编码
     * @param funcCode 功能编码
     * @param data     数据
     */
    @DS("#dbName")
    public Map<String, Object> updateSelective(String dbName, String appCode, String funcCode, Map<String, Object> data) {
        Assert.notEmpty(data, "Update data can not be empty.");
        Map<String, Object> result = new HashMap<>(2);
        List<String> columnList = configService.getModelField(appCode, funcCode);
        String tableName = configService.getTableName(appCode, funcCode);
        String primaryKey = configService.getPrimaryKey(appCode, funcCode);
        StringBuilder sqlBuffer = new StringBuilder(SqlGenerateUtil.getUpdateSql(tableName, primaryKey, columnList, data));
        log.info("update action\n params: {}\n final sql: {}", data.toString(), sqlBuffer.toString());
        DynamicDataSourceContextHolder.push(dbName);
        jdbcTemplate.update(sqlBuffer.toString());
        result.put("primaryKey", primaryKey);
        result.put("id", data.get(primaryKey));
        return result;
    }

    /**
     * 根据主键删除数据
     *
     * @param dbName   数据库名
     * @param appCode  应用编码
     * @param funcCode 功能编码
     * @param data     数据
     */
    @DS("#dbName")
    public void delete(String dbName, String appCode, String funcCode, Map<String, Object> data) {
        String tableName = configService.getTableName(appCode, funcCode);
        String primaryKey = configService.getPrimaryKey(appCode, funcCode);
        List<String> idList = (ArrayList) data.get("id");
        StringBuilder sqlBuffer = new StringBuilder("delete from " + SqlGenerateUtil.getMarkValue(tableName));
        sqlBuffer.append(" where ").append(SqlGenerateUtil.getMarkValue(primaryKey)).append(" in ");
        sqlBuffer.append(SqlGenerateUtil.getInCondition(idList));
        log.info("delete action\n params: {}\n final sql: {}", data.toString(), sqlBuffer.toString());
        DynamicDataSourceContextHolder.push(dbName);
        jdbcTemplate.update(sqlBuffer.toString());
    }

    /**
     * 根据条件查询数据
     *
     * @param dbName    数据源
     * @param appCode   应用编码
     * @param funcCode  功能编码
     * @param condition 过滤条件
     */
    @DS("#dbName")
    public List<Map<String, Object>> query(String dbName, String appCode, String funcCode, Map<String, Object> condition) {
        String tableName = configService.getTableName(appCode, funcCode);
        String queryRaw = configService.getCollection(tableName);
        String querySql = SqlGenerateUtil.getQuerySql(queryRaw, condition);
        log.info("query action\n params: {}\n final sql: {}", condition.toString(), querySql);
        DynamicDataSourceContextHolder.push(dbName);
        return jdbcTemplate.queryForList(querySql);
    }

    /**
     * 根据条件查询数据（分页）
     *
     * @param dbName         数据源
     * @param appCode        应用编码
     * @param funcCode       功能编码
     * @param queryCondition 过滤条件
     */
    @DS("#dbName")
    public List<Map<String, Object>> queryPage(String dbName, String appCode, String funcCode, JSONObject queryCondition) {
        String tableName = configService.getTableName(appCode, funcCode);
        String queryRaw = configService.getCollection(tableName);
        String querySql = SqlGenerateUtil.getQuerySql(queryRaw, queryCondition);
        log.info("query page action\n params: {}\n final sql: {}", queryCondition.toJSONString(), querySql);
        DynamicDataSourceContextHolder.push(dbName);
        return jdbcTemplate.queryForList(querySql);
    }

    /**
     * 查询记录数
     *
     * @param dbName         数据源
     * @param appCode        应用编码
     * @param funcCode       功能编码
     * @param queryCondition 过滤条件
     */
    @DS("#dbName")
    public int queryCount(String dbName, String appCode, String funcCode, JSONObject queryCondition) {
        String tableName = configService.getTableName(appCode, funcCode);
        String querySql = SqlGenerateUtil.getCountSql(tableName, queryCondition);
        log.info("query count action\n params: {}\n final sql: {}", queryCondition.toJSONString(), querySql);
        DynamicDataSourceContextHolder.push(dbName);
        return jdbcTemplate.queryForObject(querySql, Integer.class);
    }


}
