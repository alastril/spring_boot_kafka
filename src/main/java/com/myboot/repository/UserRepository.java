package com.myboot.repository;

import com.myboot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
    Page<User> findAllByDateCreation(Pageable pageable, ZonedDateTime dateCreation);
    Slice<User> findAllSliceByDateCreationBefore(Pageable pageable, ZonedDateTime dateCreationBefore);

    List<User> findByDateCreationBetween(Pageable pageable, ZonedDateTime dateCreationStart, ZonedDateTime dateCreationEnd);
}
