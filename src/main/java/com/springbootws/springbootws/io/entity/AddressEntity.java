package com.springbootws.springbootws.io.entity;

import com.springbootws.springbootws.shared.dto.UserDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "addresses")
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = 8398724563883981655L;

    @Id
    @GeneratedValue
    private long id;

    @Column(length = 30, nullable = false)
    private String addressId;
    @Column(length = 30, nullable = false)
    private String city;
    @Column(length = 30, nullable = false)
    private String country;

    @Column(length = 100, nullable = false)
    private String streetName;
    @Column(length = 7, nullable = false)
    private String postalCode;
    @Column(length = 15, nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;
}
