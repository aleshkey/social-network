package com.socialnetwork.back.service;

import com.socialnetwork.back.model.User;
import com.socialnetwork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmailIgnoreCase(username).orElseThrow(()->new UsernameNotFoundException("user not found "+ username));
        return build(user);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public static User build(User user){
        List<GrantedAuthority> authorities = user.getRole().stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
        return  new User(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), authorities);
    }
}
