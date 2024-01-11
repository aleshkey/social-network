package com.socialnetwork.back.facade;

import com.socialnetwork.back.dto.UserDTO;
import com.socialnetwork.back.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    public UserDTO userToUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstname(userDTO.getFirstname());
        userDTO.setBio(user.getBio());
        userDTO.setFirstname(user.getLastname());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }
}
