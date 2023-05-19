package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.validator.annotation.NullOrNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UserAccountDataDto(
    @NotNull(message = "{validation.user.username.notnull}")
    @NotBlank(message = "{validation.user.username.notblank}")
    String username,

    @Email(message = "{validation.user.email}")
    @NullOrNotBlank(message = "{validation.user.email.blank}")
    String email,

    @NotNull(message = "{validation.user.pw.notnull}")
    @NotBlank(message = "{validation.user.pw.notblank}")
    @Size.List ({
        @Size(min = 8, message = "{validation.user.pw.minsize}"),
        @Size(max = 255, message = "{validation.user.pw.maxsize}")
    })
    String password
){}
