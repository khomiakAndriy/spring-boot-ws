package com.springbootws.springbootws.ui.service;

import com.springbootws.springbootws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    List<AddressDTO> getAddresses(String userId0);

    AddressDTO getAddress(String addressId);
}
