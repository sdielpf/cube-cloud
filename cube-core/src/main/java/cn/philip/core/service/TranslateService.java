package cn.philip.core.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.philip.core.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 字段翻译服务
 * @author: pfliu
 * @time: 2020/6/8
 */
@Slf4j
@Service
public class TranslateService {

    @Autowired
    private ConfigService configService;
    @Autowired
    private DictService dictService;
    @Autowired
    private CommonService commonService;

    /**
     * 翻译数据schema
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     * @param dataList 数据列表
     */
    public List<Map<String, Object>> getSchema(String appCode, String funcCode, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> schemaList = new ArrayList<>();
        if (null != dataList && !dataList.isEmpty()) {
            // 查询该功能的字段配置
            List<Map<String, Object>> viewFieldList = configService.getViewField(appCode, funcCode);
            if (null != viewFieldList && !viewFieldList.isEmpty()) {
                for (Map<String, Object> viewField : viewFieldList) {
                    String source = StrUtil.toString(viewField.get("source"));
                    String fieldCode = StrUtil.toString(viewField.get("field_code"));
                    if (StrUtil.isNotBlank(source) && !"null".equalsIgnoreCase(source)) {
                        // 提供字典schema
                        if ("dict".equalsIgnoreCase(source)) {
                            String dictCode = StrUtil.toString(viewField.get("source_content"));
                            String appId = "";
                            Map<String, Object> appInfo = configService.getAppInfo(appCode);
                            if (null != appInfo) {
                                appId = StrUtil.toString(appInfo.get("id"));
                            }
                            List<Map<String, Object>> dictList = dictService.getDictItems(appId, dictCode, getValueList(fieldCode, dataList));
                            Map<String, Object> dictMap = new HashMap<>();
                            dictMap.put("fieldCode", fieldCode);
                            dictMap.put("valueList", dictList);
                            schemaList.add(dictMap);
                        }
                        // 提供功能数据schema
                        if ("func".equalsIgnoreCase(source)) {
                            String code = StrUtil.toString(viewField.get("source_content"));
                            String trueValue = StrUtil.toString(viewField.get("true_value"));
                            String showValue = StrUtil.toString(viewField.get("show_value"));
                            List<Map<String, Object>> list = getFuncSchema(appCode, code, trueValue, getValueList(fieldCode, dataList));
                            List<Map<String, Object>> resultList = new ArrayList<>();
                            for (Map<String, Object> item : list) {
                                Map<String, Object> func = new HashMap<>();
                                func.put("key", item.get(trueValue));
                                func.put("value", item.get(showValue));
                                resultList.add(func);
                            }
                            Map<String, Object> funcMap = new HashMap<>();
                            funcMap.put("fieldCode", fieldCode);
                            funcMap.put("valueList", resultList);
                            schemaList.add(funcMap);
                        }
                    }
                }
            }
        }
        return schemaList;
    }

    /**
     * 获取值列表
     *
     * @param fieldCode 字段
     * @param dataList  数据列表
     */
    private List<String> getValueList(String fieldCode, List<Map<String, Object>> dataList) {
        List<String> valueList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            valueList.add(StrUtil.toString(data.get(fieldCode)));
        }
        return valueList;
    }

    /**
     * 查询功能schema
     *
     * @param appCode  应用编码
     * @param funcCode 功能编码
     */
    private List<Map<String, Object>> getFuncSchema(String appCode, String funcCode, String fieldCode, List<String> valueList) {
        String dbName = configService.getDataSourceName(appCode, funcCode);
        JSONObject params = new JSONObject();
        params.put("pageNo", 1);
        params.put("pageSize", CommonConstants.PAGE_SIZE);
        JSONArray queryList = new JSONArray();
        JSONObject queryItem = new JSONObject();
        queryItem.put("field_code", fieldCode);
        queryItem.put("query_condition", "in");
        queryItem.put("value", "(" + getInCondition(valueList) + ")");
        queryList.add(queryItem);
        params.put("query", queryList);
        return commonService.queryPage(dbName, appCode, funcCode, params);
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
