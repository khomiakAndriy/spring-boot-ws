package com.springbootws.springbootws.ui.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

@Getter
@Setter
@NoArgsConstructor
public class AddressesRest extends ResourceSupport{

    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
}
