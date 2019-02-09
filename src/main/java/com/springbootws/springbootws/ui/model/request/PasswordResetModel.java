package com.springbootws.springbootws.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetModel {

    private String token;
    private String password;
}
