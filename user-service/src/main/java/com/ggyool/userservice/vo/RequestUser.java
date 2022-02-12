package com.ggyool.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "이메일이 비어있습니다")
    @Size(min = 2, message = "이메일은 최소 2글자 이상이어야 합니다.")
    @Email
    private String email;

    @NotNull(message = "이름이 비어있습니다")
    @Size(min = 2, message = "이름은 최소 2글자 이상이어야 합니다.")
    private String name;

    @NotNull(message = "비밀번호가 비어있습니다")
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상이어야 합니다.")
    private String pwd;
}
