package com.ggyool.userservice.dto;

import com.ggyool.userservice.vo.ResponseOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createAt;
    private String encryptedPwd;

    // TODO 나중에 ResponseOrder 대신 DTO로 테스트하자
    private List<ResponseOrder> orders;
}
