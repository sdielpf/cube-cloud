package cn.philip.core.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统变量
 *
 * @author pengfeiliu
 */
@Data
@TableName("config_variable")
public class ConfigVariable {

    @TableId
    private String id;

    private String appId;

    private String varCode;

    private String varName;

    private String varType;

    private String value;

    private long state;

    private String remark;

    private String createTime;

    private String createUser;

    private String updateTime;

    private String updateUser;

}