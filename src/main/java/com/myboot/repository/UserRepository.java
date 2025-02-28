package com.myboot.repository;

import com.myboot.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
    Page<User> findAllByDateCreation(Pageable pageable, ZonedDateTime dateCreation);
    Slice<User> findAllSliceByDateCreationBefore(Pageable pageable, ZonedDateTime dateCreationBefore);
    @Lock(LockModeType.OPTIMISTIC)
    public List<User> findByDateCreationBetween(Pageable pageable, ZonedDateTime dateCreationStart, ZonedDateTime dateCreationEnd);

    @Modifying
    @Query( value = "UPDATE User SET userName=:#{#user.userName}, dateCreation=:#{#user.dateCreation} where id=:#{#user.id}")
    void updateUser(@Param("user")User entity);
}
