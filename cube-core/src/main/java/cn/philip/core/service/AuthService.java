package cn.philip.core.service;

import cn.philip.common.entity.CubeResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: 授权中心接口
 * @author: pfliu
 * @time: 2020/6/16
 */
@FeignClient("auth")
@RequestMapping("/oauth")
public interface AuthService {

    /**
     * 查询用户信息
     *
     * @param token accessToken
     */
    @GetMapping("/getUserInfo/{token}")
    CubeResult getUserInfo(@PathVariable String token);
}
