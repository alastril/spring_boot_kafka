package com.myboot.repository;

import com.myboot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.ZonedDateTime;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Page<User> findAllWithDateCreationBefore(ZonedDateTime dateCreationBefore);
}
