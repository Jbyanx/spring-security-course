package com.bycorp.spring_security_course.service.implementation;

import com.bycorp.spring_security_course.dto.request.SaveUser;
import com.bycorp.spring_security_course.dto.response.GetUser;
import com.bycorp.spring_security_course.exception.InvalidPasswordException;
import com.bycorp.spring_security_course.persistence.entity.User;
import com.bycorp.spring_security_course.persistence.repository.UserRepository;
import com.bycorp.spring_security_course.persistence.util.Role;
import com.bycorp.spring_security_course.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerOneCustomer(SaveUser newUser) {
        validatePassword(newUser);

        User user = new User();
        user.setName(newUser.name());
        user.setUsername(newUser.username());
        user.setPassword(passwordEncoder.encode(newUser.password()));
        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }

    private void validatePassword(SaveUser newUser) {
        if(!StringUtils.hasText(newUser.password()) || !StringUtils.hasText(newUser.repeatedPassword())) {
            throw new InvalidPasswordException("Password do not match");
        }
        if (!newUser.password().equals(newUser.repeatedPassword())) {
            throw new InvalidPasswordException("Password do not match");
        }
    }

    @Override
    public Optional<User> findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @PreAuthorize("denyAll()")
    @Override
    public Page<GetUser> findAll(Pageable pageable) {
        int passwordReference = 123;

        Page<User> userList = userRepository.findAll(pageable);

        List<GetUser> getUsers = new ArrayList<>();

        for (User u : userList) {
            GetUser userdto = new GetUser(
                    u.getUsername(), u.getRole().name(), passwordReference
            );
            passwordReference += 333;
            getUsers.add(userdto);
        }

        return new PageImpl<>(getUsers, pageable, userList.getTotalElements());
    }
}
