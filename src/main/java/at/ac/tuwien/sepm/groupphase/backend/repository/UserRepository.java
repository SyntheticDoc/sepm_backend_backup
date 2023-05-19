package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long>, UserRepositoryCustom {

    @Query("SELECT u FROM ApplicationUser u WHERE u.email = :emailOrUsername or u.username = :emailOrUsername")
    ApplicationUser findUserByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);

    @Query("SELECT u FROM ApplicationUser u WHERE u.username = :username")
    ApplicationUser findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM ApplicationUser u WHERE u.loginKey = :loginKey")
    ApplicationUser findUserByLoginKey(@Param("loginKey") String loginKey);

}
