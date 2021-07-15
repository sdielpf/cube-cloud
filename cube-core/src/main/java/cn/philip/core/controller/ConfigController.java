package cn.philip.core.controller;

import cn.philip.common.entity.CubeResult;
import cn.philip.core.config.CheckApp;
import cn.philip.core.service.ConfigService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 页面配置查询接口
 * @author: pfliu
 * @time: 2020/5/21
 */
@Slf4j
@CheckApp
@RestController
@RequestMapping("/{appCode}/api")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @ApiOperation(value = "查询过滤器")
    @GetMapping(value = "/getFilters/{funcCode}")
    public CubeResult getFilters(@PathVariable String appCode, @PathVariable String funcCode) {
        CubeResult message = new CubeResult();
        message.setData(configService.getFilter(appCode, funcCode));
        return message;
    }

    @ApiOperation(value = "查询表单字段")
    @GetMapping(value = "/getColumns/{funcCode}")
    public CubeResult getColumns(@PathVariable String appCode, @PathVariable String funcCode) {
        CubeResult message = new CubeResult();
        message.setData(configService.getViewField(appCode, funcCode));
        return message;
    }

    @ApiOperation(value = "查询按钮", notes = "此功能用于查询功能的按钮")
    @GetMapping(value = "/getButtons/{funcCode}")
    public CubeResult getButtons(@PathVariable String appCode, @PathVariable String funcCode) {
        CubeResult message = new CubeResult();
        message.setData(configService.getButtons(appCode, funcCode));
        return message;
    }
}
