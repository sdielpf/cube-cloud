package cn.philip.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.philip.common.exception.CubeException;
import cn.philip.core.constants.CommonConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @description: SQL语句拼凑工具
 * @author: pfliu
 * @time: 2020/5/22
 */
public class SqlGenerateUtil {

    public static final String MYSQL_MARK = "`";

    /**
     * 构建插入指定表指定列的sql
     *
     * @param tableName  表名
     * @param columnList 列
     */
    public static String getInsertSql(String tableName, List<String> columnList) {
        Assert.notNull(tableName, "Table name must not empty.");
        Assert.notEmpty(columnList, "Insert columns can not empty.");
        StringBuilder sqlBuffer = new StringBuilder("replace into ");
        sqlBuffer.append(getMarkValue(tableName));
        StringBuilder tmpValuesBuffer = new StringBuilder("(?");
        sqlBuffer.append("(").append(getMarkValue(columnList.get(0)));
        for (int i = 1; i < columnList.size(); i++) {
            sqlBuffer.append(",");
            sqlBuffer.append(getMarkValue(columnList.get(i)));
            tmpValuesBuffer.append(",").append("?");
        }
        sqlBuffer.append(") values ");
        sqlBuffer.append(tmpValuesBuffer.toString()).append(")");
        return sqlBuffer.toString();
    }

    /**
     * 构建修改指定表指定列的sql(全量）
     *
     * @param tableName  表名
     * @param idName     主键
     * @param columnList 列
     */
    public static String getUpdateSql(String tableName, String idName, List<String> columnList) {
        Assert.notNull(tableName, "Table can not null.");
        Assert.notNull(idName, "Id's name can not null.");
        Assert.notEmpty(columnList, "Columns can not empty.");
        StringBuilder sqlBuffer = new StringBuilder("update " + getMarkValue(tableName));
        sqlBuffer.append(" set ").append(getMarkValue(columnList.get(0))).append(" = ? ");
        for (int i = 1; i < columnList.size(); i++) {
            sqlBuffer.append(",");
            sqlBuffer.append(getMarkValue(columnList.get(i))).append(" = ? ");
        }
        sqlBuffer.append(" where ").append(getMarkValue(idName)).append(" = ? ");
        return sqlBuffer.toString();
    }

    /**
     * 构建指定表，指定列中存在需要更新数据的sql
     *
     * @param tableName  表名
     * @param idName     主键
     * @param columnList 列
     * @param data       数据
     */
    public static String getUpdateSql(String tableName, String idName, List<String> columnList, Map<String, Object> data) {
        Assert.notNull(tableName, "Table can not null.");
        Assert.notNull(idName, "Id's name can not null.");
        Assert.notEmpty(columnList, "Columns can not empty.");
        if (null == data.get(idName) || "NULL".equals(StrUtil.toString(data.get(idName)))) {
            throw new CubeException(500, idName + " is required.");
        }
        StringBuilder sqlBuffer = new StringBuilder("update " + getMarkValue(tableName));
        sqlBuffer.append(" set ");
        for (String key : data.keySet()) {
            if (columnList.contains(key) && null != data.get(key) && !key.equals(idName)) {
                sqlBuffer.append(getMarkValue(key)).append(" =  '").append(data.get(key)).append("'");
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" where ").append(getMarkValue(idName)).append(" = '").append(data.get(idName)).append("'");
        return sqlBuffer.toString();
    }

    /**
     * 拼凑查询sql
     *
     * @param queryRaw  查询条件
     * @param condition 条件
     */
    public static String getQuerySql(String queryRaw, Map<String, Object> condition) {
        StringBuilder querySql = new StringBuilder("SELECT * FROM (").append(queryRaw);
        querySql.append(") t WHERE 1 = 1");
        if (null != condition && !condition.isEmpty()) {
            for (String key : condition.keySet()) {
                querySql.append(" AND t.").append(getMarkValue(key));
                querySql.append(" = ").append("'").append(condition.get(key)).append("'");
            }
        }
        querySql.append(" LIMIT 0, ").append(CommonConstants.PAGE_SIZE_MAX);
        return querySql.toString();
    }

    /**
     * 拼凑查询sql
     *
     * @param queryRaw       查询sql
     * @param queryCondition 条件(高级）
     */
    public static String getQuerySql(String queryRaw, JSONObject queryCondition) {
        int pageNo = queryCondition.getInteger("pageNo") == 0 ? 1 : queryCondition.getInteger("pageNo");
        int pageSize = queryCondition.getInteger("pageSize") == 0 ? CommonConstants.PAGE_SIZE : queryCondition.getInteger("pageSize");
        String sort = queryCondition.getString("sort");
        JSONArray conditionList = queryCondition.getJSONArray("query");
        StringBuilder querySql = new StringBuilder("SELECT * FROM (").append(queryRaw);
        querySql.append(") t WHERE 1 = 1");
        if (null != conditionList && !conditionList.isEmpty()) {
            for (int i = 0; i < conditionList.size(); i++) {
                JSONObject condition = conditionList.getJSONObject(i);
                querySql.append(" AND t.").append(getMarkValue(condition.getString("field_code")))
                        .append(" ").append(condition.getString("query_condition"));
                if ("like".equals(condition.getString("query_condition"))) {
                    querySql.append(" '").append("%").append(condition.getString("value")).append("%' ");
                } else if ("in".equalsIgnoreCase(condition.getString("query_condition"))) {
                    querySql.append(condition.getString("value"));
                } else {
                    querySql.append(" '").append(condition.getString("value")).append("' ");
                }
            }
        }
        if (StrUtil.isNotBlank(sort)) {
            querySql.append(" ORDER BY ").append(sort);
        }
        querySql.append(" LIMIT ").append((pageNo - 1) * pageSize).append(" , ").append(pageSize);
        return querySql.toString();
    }

    /**
     * 查询记录数
     *
     * @param tableName 表名
     * @param condition 条件
     */
    public static String getCountSql(String tableName, Map<String, Object> condition) {
        StringBuilder querySql = new StringBuilder("SELECT count(*) FROM " + getMarkValue(tableName));
        querySql.append(" WHERE 1 = 1");
        if (null != condition && !condition.isEmpty()) {
            for (String key : condition.keySet()) {
                querySql.append(" AND ").append(getMarkValue(key));
                querySql.append(" = ").append("'").append(condition.get(key)).append("'");
            }
        }
        return querySql.toString();
    }

    /**
     * 查询记录数
     *
     * @param tableName      表名
     * @param queryCondition 查询条件
     */
    public static String getCountSql(String tableName, JSONObject queryCondition) {
        JSONArray conditionList = queryCondition.getJSONArray("query");
        StringBuilder querySql = new StringBuilder("SELECT count(*) FROM " + getMarkValue(tableName));
        querySql.append(" WHERE 1 = 1");
        if (null != conditionList && !conditionList.isEmpty()) {
            for (int i = 0; i < conditionList.size(); i++) {
                JSONObject condition = conditionList.getJSONObject(i);
                querySql.append(" AND ").append(getMarkValue(condition.getString("field_code")))
                        .append(" ").append(condition.getString("query_condition")).append(" '");
                if ("like".equals(condition.getString("query_condition"))) {
                    querySql.append("%").append(condition.getString("value")).append("%' ");
                } else {
                    querySql.append(condition.getString("value")).append("' ");
                }
            }
        }
        return querySql.toString();
    }

    /**
     * 按指定值构建in列表的sql语句
     *
     * @param values 指定in值得列表
     */
    public static String getInCondition(List<String> values) {
        Assert.notEmpty(values, "Condition values can not empty.");
        StringBuilder inCondition = new StringBuilder("('" + StrUtil.toString(values.get(0)) + "'");
        for (int i = 1; i < values.size(); i++) {
            inCondition.append(",");
            inCondition.append("'").append(StrUtil.toString(values.get(i))).append("'");
        }
        inCondition.append(") ");
        return inCondition.toString();
    }

    /**
     * 获取mysql带标示的值
     *
     * @param value 表名
     */
    public static String getMarkValue(String value) {
        return MYSQL_MARK + value + MYSQL_MARK;
    }
}
