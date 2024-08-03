
package com.example.Repositories;

import com.example.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "select * from user where phone_number=?1",nativeQuery = true)
    User getByPhone(String Phone);
}
