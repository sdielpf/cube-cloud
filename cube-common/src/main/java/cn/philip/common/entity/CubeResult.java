package cn.philip.common.entity;

import lombok.Data;

/**
 * 通用消息返回体
 *
 * @author pengfeiliu
 * @date 2002/05
 */
@Data
public class CubeResult {

    private int code;

    private String message;

    private Object data;


    public CubeResult() {
        this.code = 200;
        this.message = "success";
    }

    public CubeResult(Object data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
    }

    public CubeResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CubeResult(int code, String message, Object data) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
}
