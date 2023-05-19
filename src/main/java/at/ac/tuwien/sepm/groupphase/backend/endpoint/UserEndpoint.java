package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.UserRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRegistrationService userRegService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserRegistrationService userRegService,
                        UserService userService,
                        UserMapper userMapper) {
        this.userRegService = userRegService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured("ROLE_USER") // + service does additional check
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Get info on a user")
    public List<UserAccountDataDto> find(@Valid UserFindDto userFindDto, Authentication auth) {
        LOGGER.info("POST /api/v1/users {}", userFindDto);
        return userService.findUsers(userFindDto, auth)
            .stream()
            .map(userMapper::applicationUserToUserAccountDataDto)
            .collect(Collectors.toList());
    }

    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Register a new user")
    public String register(@Valid @RequestBody UserAccountDataDto registerDto,
                           Locale locale) {
        LOGGER.info("POST /api/v1/users {} locale:{}", registerDto, locale);
        return userRegService.registerUser(locale, registerDto);
    }

    @Secured("ROLE_USER") // + service does additional check
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @Operation(summary = "Delete a user")
    public void delete(@RequestParam String username, Authentication auth) {
        LOGGER.info("DELETE /api/v1/users");
        userService.deleteUser(username, auth);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    @Operation(summary = "Update current user")
    public void update(@Valid @RequestBody UserAccountDataUpdateDto data, Locale locale, Authentication auth) {
        LOGGER.info("PUT /api/v1/users {}", data);
        userService.updateUser(data, locale, auth);
    }

}
