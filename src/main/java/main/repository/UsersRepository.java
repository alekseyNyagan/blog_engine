package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    public User findUserById(int id);
    public Optional<User> findUserByEmail(String email);
    public Optional<User> findUserByCode(String code);

    @Query(nativeQuery = true, value = "UPDATE users SET code = :code WHERE email = :email")
    @Transactional
    @Modifying
    public void updateUserCode(@Param("code") String code, @Param("email") String email);

    @Query(nativeQuery = true, value = "UPDATE users SET password = :password WHERE id = :id")
    @Transactional
    @Modifying
    public void updatePassword(@Param("password") String password, @Param("id") int id);
}
