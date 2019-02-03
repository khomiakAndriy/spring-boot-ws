package com.springbootws.springbootws.ui.service.impl;

import com.springbootws.springbootws.io.entity.UserEntity;
import com.springbootws.springbootws.shared.Utils;
import com.springbootws.springbootws.shared.dto.UserDto;
import com.springbootws.springbootws.ui.model.response.ErrorMessages;
import com.springbootws.springbootws.ui.repository.UserRepository;
import com.springbootws.springbootws.ui.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    private ModelMapper mapper = new ModelMapper();

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Utils utils;

    @Override
    public UserDto createUser(UserDto userDto) {

        if (userRepository.findByEmail(userDto.getEmail()) != null){
            throw new RuntimeException("Record already exist");
        }

        userDto.getAddresses().forEach(a -> {
            a.setAddressId(utils.generateGddressId(30));
            a.setUserDetails(userDto);
        });

//        UserEntity userEntity = new UserEntity();
//        BeanUtils.copyProperties(userDto, userEntity);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);

        userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(Boolean.FALSE);

        UserEntity storedUserEntity = userRepository.save(userEntity);

//        UserDto returnValue = new UserDto();
//        BeanUtils.copyProperties(storedUserEntity, returnValue);
//        UserDto returnValue =  mapper.map(storedUserEntity, UserDto.class);

        return mapper.map(storedUserEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null){
            throw new UsernameNotFoundException(email);
        }
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(entity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userid) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userid);

        if (userEntity == null){

            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null){
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
//        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
        return new User(userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,
                true,
                true,
                new ArrayList<>()
        );
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null) {

            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {

            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> entityPage = userRepository.findAll(pageableRequest);

        entityPage.getContent().forEach(userEntity -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        });

        return returnValue;
    }

    @Override
    public boolean verifyByEmailToken(String token) {
        boolean result = false;
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null){
            boolean hasTokenExpired = utils.hasTokenExpired(token);
            if (!hasTokenExpired){
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                result = true;
            }
        }
        return result;
    }
}
