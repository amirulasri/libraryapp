package com.swc4253groupd.libraryapp.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.swc4253groupd.libraryapp.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
