package cn.philip.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台通用异常
 *
 * @author pengfeiliu
 * @date 2002/05
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CubeException extends RuntimeException {

    private Integer code;

    private String message;

    public CubeException(int code) {
        this.code = code;
    }

    public CubeException(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
