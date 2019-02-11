package com.springbootws.springbootws.ui.controller;

import com.springbootws.springbootws.exception.UserServiceException;
import com.springbootws.springbootws.security.SecurityConstants;
import com.springbootws.springbootws.shared.AmazonSES;
import com.springbootws.springbootws.shared.dto.AddressDTO;
import com.springbootws.springbootws.shared.dto.UserDto;
import com.springbootws.springbootws.ui.model.request.PasswordResetModel;
import com.springbootws.springbootws.ui.model.request.PasswordResetRequestModel;
import com.springbootws.springbootws.ui.model.request.UserDetailsRequestModel;
import com.springbootws.springbootws.ui.model.response.*;
import com.springbootws.springbootws.ui.service.AddressService;
import com.springbootws.springbootws.ui.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;
    @Autowired
    private AmazonSES amazonSES;

    private ModelMapper mapper = new ModelMapper();

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)  {

//        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REAUIRED_FIELD.getErrorMessage());
        }

//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);


        UserDto userDto = mapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        amazonSES.verifyEmail(createdUser);

//        BeanUtils.copyProperties(createdUser, returnValue);
        UserRest returnValue = mapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id){
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PutMapping(path="/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails){
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REAUIRED_FIELD.getErrorMessage());
        }
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id){

        OperationStatusModel status = new OperationStatusModel();
        status.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(id);
        status.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return status;
    }

    @ApiOperation(value = "Get users details endpoint", notes = "description")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "autorization", value = "Bearer JWT token", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {


        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        users.forEach(userDto -> {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnValue.add(userRest);
        });
        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public Resources<AddressesRest> getAddresses(@PathVariable String id){

        List<AddressesRest> returnValue = new ArrayList<>();
        List<AddressDTO> addresses = addressService.getAddresses(id);

        if (addresses != null && !addresses.isEmpty()){

            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = mapper.map(addresses, listType);
        }

        returnValue.forEach(v -> {
            Link addressLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class).getUserAddress(id, v.getAddressId())).withSelfRel();
            Link userLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class).getUser(id)).withRel("user");
            v.add(addressLink, userLink);
        });

        return new Resources<>(returnValue);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId){

        AddressDTO addresses = addressService.getAddress(addressId);
//        Link addressLink = ControllerLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
        Link addressLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = ControllerLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = ControllerLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");

        AddressesRest result = mapper.map(addresses, AddressesRest.class);
        result.add(addressLink, userLink, addressesLink);

        return new Resource<>(result);
    }

    @GetMapping(path = "/email-verification", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public OperationStatusModel verifyByEmailToken(@RequestParam(value = "token") String token){

        OperationStatusModel result = new OperationStatusModel();
        result.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyByEmailToken(token);

        if (isVerified){
            result.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            result.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return result;
    }

    /*
     * http://localhost:8080/mobile-app-ws/users/password-reset-request
     * */
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
