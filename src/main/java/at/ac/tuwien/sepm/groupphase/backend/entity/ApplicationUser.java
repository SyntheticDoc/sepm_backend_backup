package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Entity
@Table(name = "users")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    @NotBlank
    private String username;

    @Column(unique = true)
    @Email
    private String email;

    /**
     * Hashed password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Auth roles this user has.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private Set<@Pattern(regexp = "ROLE_[A-Z0-9]{1,255}") String> roles;

    /**
     * This key can be used to log the user in
     * and re-enable the account.
     */
    @Column(unique = true)
    private String loginKey;

    /**
     * While this is false, the user may login
     * only using the loginKey.
     */
    @Column(nullable = false)
    private Boolean enabled;

    /**
     * While this is false, the user may not log in at all.
     */
    @Column(nullable = false)
    private Boolean locked;

    public ApplicationUser() {
    }

    public ApplicationUser(Long id,
                           String username,
                           String email,
                           String password,
                           Set<String> roles,
                           String loginKey,
                           Boolean enabled,
                           Boolean locked) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.loginKey = loginKey;
        this.enabled = enabled;
        this.locked = locked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }
}
