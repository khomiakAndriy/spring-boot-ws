package com.springbootws.springbootws.ui.service.impl;

import com.springbootws.springbootws.io.entity.AddressEntity;
import com.springbootws.springbootws.io.entity.UserEntity;
import com.springbootws.springbootws.shared.dto.AddressDTO;
import com.springbootws.springbootws.ui.repository.AddressRepository;
import com.springbootws.springbootws.ui.repository.UserRepository;
import com.springbootws.springbootws.ui.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    private ModelMapper mapper = new ModelMapper();

    @Override
    public List<AddressDTO> getAddresses(String userId0) {

        List<AddressDTO> result = new ArrayList<>();
        UserEntity entity = userRepository.findByUserId(userId0);


        if (entity == null){

            return result;
        }

        List<AddressEntity> allByUserDetails = addressRepository.findAllByUserDetails(entity);

        allByUserDetails.forEach(v -> result.add(mapper.map(v, AddressDTO.class)));

        return result;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO result = new AddressDTO();
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity == null){

            throw new RuntimeException();
        }

        return mapper.map(addressEntity, AddressDTO.class);
    }
}
