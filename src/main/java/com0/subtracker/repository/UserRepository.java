package com0.subtracker.repository;

import com0.subtracker.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
/**
 * This interface implements the basic CRUD
 * Additional methods can be defined here such as findByXX
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.details
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
}

