package com.socialnetwork.back.service;

import com.socialnetwork.back.dto.UserDTO;
import com.socialnetwork.back.error.UserExistException;
import com.socialnetwork.back.model.User;
import com.socialnetwork.back.model.enums.ERole;
import com.socialnetwork.back.payload.request.SignupRequest;
import com.socialnetwork.back.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User create(SignupRequest userIn){
        User user = new User();
        user.setEmail(userIn.getEmail());
        user.setName(userIn.getFirstname());
        user.setLastname(userIn.getLastname());
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRole().add(ERole.ROLE_USER);
        try {
            LOG.info("Saving user {}", user.getEmail());
            return userRepository.save(user);
        }
        catch (Exception e){
            LOG.error("Error saving. {}", e.getMessage());
            throw new UserExistException("User "+ user.getEmail()+ "already exist");
        }
    }

    public User update(UserDTO userDTO, Principal principal){
        User user = getUserByPrincipal(principal);
        user.setName(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setBio(userDTO.getBio());
        return userRepository.save(user);
    }

    public User getCurrent(Principal principal){
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal){
        return userRepository.findByUsernameIgnoreCase(principal.getName()).orElseThrow(() -> {
            throw new UsernameNotFoundException("User "+principal.getName()+" not found");
        });
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
