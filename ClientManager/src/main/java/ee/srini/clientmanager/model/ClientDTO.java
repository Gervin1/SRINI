package ee.srini.clientmanager.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    @NotEmpty(message = "First name is required")
    private String firstName;
    @NotEmpty(message = "Last name is required")
    private String lastName;
    @NotEmpty(message = "Username is required")
    private String username;
    private String email;
    @NotEmpty(message = "Address is required")
    private String address;
    @NotNull(message = "Country is required")
    private CountryDTO country;
    private Date createdAt;
}
