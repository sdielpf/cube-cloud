package cn.philip.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 系统用户
 * @author: pfliu
 * @time: 2020/6/9
 */
@Data
public class SysUser implements Serializable {

    private String id;
    private String appId;
    private String userCode;
    private String userName;
    private String password;
    private String orgId;
    private String gender;
    private String birthday;
    private String idNumber;
    private String email;
    private String tel;
    private String intoDate;
    private String salt;
    private String lastLoginTime;
    private String userSource;
    private String state;
    private String remark;
    private String createTime;
    private String createUser;
    private String updateTime;
    private String updateUser;

}
