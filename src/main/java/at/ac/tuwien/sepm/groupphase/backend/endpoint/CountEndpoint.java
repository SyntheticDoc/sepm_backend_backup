package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/counts")
public class CountEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;

    @Autowired
    public CountEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @GetMapping("/users")
    @Operation(summary = "Check how many users are matched by the given find criteria")
    public int findUserCount(@Valid UserFindDto userFindDto) {
        LOGGER.info("GET /api/v1/counts/users {}", userFindDto);
        return userService.findUserCount(userFindDto);
    }
}
