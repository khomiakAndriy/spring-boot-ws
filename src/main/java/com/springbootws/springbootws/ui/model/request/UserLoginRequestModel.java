package com.springbootws.springbootws.ui.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class UserLoginRequestModel {

    private String email;
    private String password;
}

