package com0.subtracker.service;

import com0.subtracker.exception.UserException;
import com0.subtracker.model.User;
import com0.subtracker.repository.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;

    private SecurityService securityService;

    @Autowired
    public UserService(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    @Transactional
    public User create(@NonNull User user) throws UserException {
        // Check if user exist
        Optional<User> retrievedUser = userRepository.findById(securityService.getUser().getUserID());

        if (retrievedUser.isEmpty()){
            user.setUserID(securityService.getUser().getUserID());
            return userRepository.save(user);
        } else{
            throw new UserException("User already exist in database");
        }
    }

    @Transactional
    public User findOne(@NonNull String id) throws UserException{
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            return user.get();
        }else{
            throw new UserException("User does not exist in database");
        }
    }

    @Transactional
    public User update(@NonNull User user){
        //TODO: Consider using a mapper framework
        return userRepository.save(user);
    }

    @Transactional
    public void delete(@NonNull String id) {
        userRepository.deleteById(id);
    }
}
