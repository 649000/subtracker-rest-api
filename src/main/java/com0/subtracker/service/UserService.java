package com0.subtracker.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com0.subtracker.exception.UserException;
import com0.subtracker.model.User;
import com0.subtracker.repository.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
        Optional<User> optionalUser = userRepository.findById(securityService.getUser().getUserID());

        if (optionalUser.isEmpty()) {
            user.setUserID(securityService.getUser().getUserID());
            return userRepository.save(user);
        } else {
            throw new UserException("User already exist in database");
        }
    }

    @Transactional
    public User findOne(@NonNull String id) throws UserException {
        if (validUser(id)) {
            return userRepository.findById(id).get();
        } else {
            throw new UserException("User logged in != User retrieved");
        }
    }

    @Transactional(rollbackOn = {FirebaseAuthException.class, DataAccessException.class})
    public User update(@NonNull User user) throws UserException {
        if (validUser(user.getUserID())) {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getUserID())
                    .setEmail(user.getEmail())
                    .setEmailVerified(true)
                    .setDisplayName(user.getName())
                    .setDisabled(false);

            try {
                FirebaseAuth.getInstance().updateUser(request);
                return userRepository.save(user);
            } catch (FirebaseAuthException e) {
                log.error("FirebaseAuthException", e);
                throw new UserException("Exception in updating Firebase Auth");
            } catch (DataAccessException e) {
                log.error("DataAccessException", e);
                throw new UserException("Exception in updating database");
            }
        } else {
            throw new UserException("User logged in != User being updated");
        }
    }

    @Transactional(rollbackOn = {FirebaseAuthException.class, DataAccessException.class})
    public void delete(@NonNull String id) throws UserException {
        if (validUser(id)) {
            try {
                FirebaseAuth.getInstance().deleteUser(id);
                userRepository.deleteById(id);
            } catch (FirebaseAuthException e) {
                log.error("FirebaseAuthException", e);
                throw new UserException("Exception in updating Firebase Auth");
            } catch (DataAccessException e) {
                log.error("DataAccessException", e);
                throw new UserException("Exception in updating database");
            }
        } else {
            throw new UserException("User logged in != User being deleted");
        }

    }

    /**
     * @param id
     * @return
     */
    private boolean validUser(String id) {
        Optional<User> optionalUser = userRepository.findById(securityService.getUser().getUserID());
        if (optionalUser.isPresent()) {
            if (securityService.getUser().getUserID().equals(id)) {
                return true;
            } else {
                log.error("User does not match. Request User: {}, Logged In User: {}", id, securityService.getUser().getUserID());
                return false;
            }
        } else {
            log.error("User does not exist in database");
            return false;
        }
    }
}
