package com.socialnetwork.back.controller;

import com.socialnetwork.back.dto.UserDTO;
import com.socialnetwork.back.facade.UserFacade;
import com.socialnetwork.back.model.User;
import com.socialnetwork.back.service.UserService;
import com.socialnetwork.back.validators.ResponseErrorValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private ResponseErrorValidator responseErrorValidator;

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal){
        User user = userService.getCurrent(principal);
        UserDTO userDTO = userFacade.userToUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId){
        User user = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = userFacade.userToUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Principal principal){
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (ObjectUtils.isEmpty(errors)) return errors;

        User user = userService.update(userDTO, principal);

        UserDTO userUpdated = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

}
