package com.subtracker.service;

import com.subtracker.exception.SubscriptionException;
import com.subtracker.repository.UserRepository;
import com.subtracker.model.Subscription;
import com.subtracker.model.User;
import com.subtracker.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SubscriptionService {

    private SubscriptionRepository subscriptionRepository;

    private SecurityService securityService;

    private UserRepository userRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, SecurityService securityService, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    /**
     * Create a new subscription
     * @param subscription
     * @return
     */
    @Transactional
    public Subscription create(@NonNull Subscription subscription) throws SubscriptionException {
        User user = validUser();
        user.getSubList().add(subscription);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription findOne(@NonNull UUID id) throws SubscriptionException {
        Optional<Subscription> subscription = subscriptionRepository.findById(id);
        if (subscription.isPresent()){
            return subscription.get();
        } else {
            log.error("Subscription ID: {} doesn't exist", id);
            throw new SubscriptionException(String.format("Subscription ID :'%s' does not exist in database", id.toString()));
        }
    }

    @Transactional
    public List<Subscription> findAll() throws SubscriptionException{
        return validUser().getSubList();
    }

    @Transactional
    public Subscription update(@NonNull Subscription subscription) throws SubscriptionException {
        User user = validUser();
        user.getSubList().add(subscription);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void delete(@NonNull UUID id){
        subscriptionRepository.deleteById(id);
    }

    private User validUser() throws SubscriptionException {
        String loggedInUserID = securityService.getUser().getUserID();
        Optional<User> optionalUser = userRepository.findById(loggedInUserID);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else{
            log.error("User {} does not exist in database");
            throw new SubscriptionException(String.format("User ID :'%s' does not exist in database", loggedInUserID));
        }
    }
}
