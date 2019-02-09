package com.springbootws.springbootws.io.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "password_reset_token")
@Getter
@Setter
public class PasswordResetTokenEntity implements Serializable{

    private static final long serialVersionUID = 7263251167325947789L;

    @Id
    @GeneratedValue
    private long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;
}
