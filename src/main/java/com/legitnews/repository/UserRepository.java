package com.legitnews.repository;

import com.legitnews.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
  Optional<User> findByEmail(String email);
    @Query("select u from User u where u.membershipRequested = true")
  List<User> findAllMembershipRequests();

  @Query("select u from User u")
  List<User> findAllUsers(); // or use built-in findAll()
}
