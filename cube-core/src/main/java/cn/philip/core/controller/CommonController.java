package cn.philip.core.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import cn.philip.common.entity.CubeResult;
import cn.philip.common.exception.CubeException;
import cn.philip.core.config.CheckApp;
import cn.philip.core.constants.CommonConstants;
import cn.philip.core.service.CommonService;
import cn.philip.core.service.ConfigService;
import cn.philip.core.service.FileService;
import cn.philip.core.service.TranslateService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 通用数据接口
 * @author: pfliu
 * @time: 2020/5/20
 */
@Slf4j
@CheckApp
@RestController
@RequestMapping("/{appCode}/api")
public class CommonController {

    @Value("${upload.filePath}")
    private String localPath;

    @Autowired
    private ConfigService configService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private TranslateService translateService;
    @Autowired
    private FileService fileService;

    @ApiOperation(value = "/通用新增/修改方法（全量）", notes = "修改时请谨慎调用，如果有字段未携带值，会被置空")
    @PostMapping("/{funcCode}")
    public CubeResult saveOrUpdate(@PathVariable String appCode, @PathVariable String funcCode, @RequestBody Map<String, Object> data) {
        String dbName = configService.getDataSourceName(appCode, funcCode);
        Map<String, Object> result = commonService.saveOrUpdate(dbName, appCode, funcCode, data);
        return new CubeResult(result);
    }

    @ApiOperation("/通用修改方法")
    @PutMapping("/{funcCode}")
    public CubeResult updateSelective(@PathVariable String appCode, @PathVariable String funcCode, @RequestBody Map<String, Object> data) {
        String dbName = configService.getDataSourceName(appCode, funcCode);
        Map<String, Object> result = commonService.updateSelective(dbName, appCode, funcCode, data);
        return new CubeResult(result);
    }

    @ApiOperation("/通用删除方法")
    @DeleteMapping("/{funcCode}")
    public CubeResult delete(@PathVariable String appCode, @PathVariable String funcCode, @RequestBody Map<String, Object> data) {
        String dbName = configService.getDataSourceName(appCode, funcCode);
        commonService.delete(dbName, appCode, funcCode, data);
        return new CubeResult();
    }

    @ApiOperation("/通用查询方法")
    @PostMapping("/getDataList/{funcCode}")
    public CubeResult getDataList(@PathVariable String appCode, @PathVariable String funcCode, @RequestBody Map<String, Object> condition) {
        CubeResult message = new CubeResult();
        String dbName = configService.getDataSourceName(appCode, funcCode);
        List<Map<String, Object>> dataList = commonService.query(dbName, appCode, funcCode, condition);
        message.setData(dataList);
        return message;
    }

    @ApiOperation("批量保存/修改")
    @PostMapping("/batchSaveOrUpdate/{funcCode}")
    public CubeResult batchSaveOrUpdate(@PathVariable String appCode, @PathVariable String funcCode, @RequestBody List<Map<String, Object>> dataList) {

        return new CubeResult();
    }

    @ApiOperation("根据条件查询数据")
    @PostMapping("/getPageData/{funcCode}")
    public CubeResult getPageData(@PathVariable String appCode, @PathVariable String funcCode, @RequestBody JSONObject queryParams) {
        CubeResult message = new CubeResult();
        if (null == queryParams.get("pageNo") || null == queryParams.get("pageSize")) {
            throw new CubeException(500, "pageNo and pageSize are required to specified");
        }
        int pageSize = queryParams.getInteger("pageSize");
        if (pageSize > CommonConstants.PAGE_SIZE_MAX) {
            throw new CubeException(500, "pageSize is too large, limit it under 5000 please...");
        }
        int pageNo = queryParams.getInteger("pageNo") == 0 ? 1 : queryParams.getInteger("pageNo");
        pageSize = queryParams.getInteger("pageSize") == 0 ? CommonConstants.PAGE_SIZE : queryParams.getInteger("pageSize");
        queryParams.put("pageNo", pageNo);
        queryParams.put("pageSize", pageSize);
        String dbName = configService.getDataSourceName(appCode, funcCode);
        JSONObject result = new JSONObject();
        List<Map<String, Object>> dataList = commonService.queryPage(dbName, appCode, funcCode, queryParams);
        result.put("dataList", dataList);
        List<Map<String, Object>> schema = translateService.getSchema(appCode, funcCode, dataList);
        result.put("schema", schema);
        int totalRecords = commonService.queryCount(dbName, appCode, funcCode, queryParams);
        int totalPage = totalRecords > 0 ? (totalRecords - 1) / pageSize + 1 : 0;
        JSONObject pagination = new JSONObject();
        pagination.put("totalPage", totalPage);
        pagination.put("totalRecords", totalRecords);
        pagination.put("pageNo", pageNo);
        pagination.put("pageSize", pageSize);
        result.put("pagination", pagination);
        message.setData(result);
        return message;
    }

    /**
     * 功能描述:
     * 上传文件到本地（非mongo）
     *
     * @param request request
     * @author pfliu
     * @date 2020/05/12
     */
    @PostMapping(value = "/upload")
    @ApiOperation(value = "上传文件", notes = "此功能用于文件上传")
    public CubeResult uploadLocalFile(@PathVariable String appCode, HttpServletRequest request) throws Exception {
        StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
        Iterator<String> iterator = req.getFileNames();
        List<String> fileNameList = new ArrayList<>();
        while (iterator.hasNext()) {
            MultipartFile file = req.getFile(iterator.next());
            String fileNames = file.getOriginalFilename();
            log.info("====> upload file :" + fileNames);
            //存储文件
            String fileName = fileNames;
            String filetype = "";
            if (StrUtil.isNotEmpty(fileNames) && fileNames.contains(".")) {
                fileName = fileNames.substring(0, fileNames.lastIndexOf("."));
                filetype = fileNames.substring(fileNames.lastIndexOf(".") + 1);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String dateStr = format.format(new Date());
            fileService.saveLocalFile(fileName, filetype, localPath + dateStr, file);
            fileNameList.add(fileNames);
        }
        return new CubeResult(fileNameList);
    }

}
