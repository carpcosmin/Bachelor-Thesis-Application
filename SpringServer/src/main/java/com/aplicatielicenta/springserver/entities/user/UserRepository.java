package com.aplicatielicenta.springserver.entities.user;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE user_id = :id", nativeQuery = true)
    User findById(@Param("id") Long id);

    @Query(value = "SELECT * FROM users WHERE token = :token", nativeQuery = true)
    User findByToken(@Param("token") String token);

    User deleteById(Long id);

}
