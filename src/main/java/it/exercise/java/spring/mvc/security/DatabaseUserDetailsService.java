package it.exercise.java.spring.mvc.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.exercise.java.spring.mvc.model.User;
import it.exercise.java.spring.mvc.repository.UserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

  @Autowired
  UserRepository userRep;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Optional<User> userByUsername = userRep.findByUsername(username);

    if (userByUsername.isPresent()) {

      return new DatabaseUserDetails(userByUsername.get());

    } else {

      throw new UsernameNotFoundException("Username not found");

    }
  }

}