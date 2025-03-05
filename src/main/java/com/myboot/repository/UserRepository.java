package com.myboot.repository;

import com.myboot.entity.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
    Page<User> findAllByDateCreation(Pageable pageable, ZonedDateTime dateCreation);
    Slice<User> findAllSliceByDateCreationBefore(Pageable pageable, ZonedDateTime dateCreationBefore);
    @Lock(LockModeType.OPTIMISTIC)
    List<User> findByDateCreationBetween(Pageable pageable, ZonedDateTime dateCreationStart, ZonedDateTime dateCreationEnd);

    @Modifying
    @Query( value = "UPDATE User SET userName=:#{#user.userName}, dateCreation=:#{#user.dateCreation} where id=:#{#user.id}")
    void updateUser(@Param("user")User entity);

    @Override
    @EntityGraph(attributePaths = {"orderList"})//in one select will get all orders for every user
    @QueryHints(value = { @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true")})
    Iterable<User> findAll();

    @Override
    @EntityGraph(attributePaths = {"orderList"})
    Optional<User> findById(Long aLong);

}
