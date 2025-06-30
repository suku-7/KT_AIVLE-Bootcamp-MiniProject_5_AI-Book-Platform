package thminiprojthebook.auth;

import lombok.Data;

@Data
public class AuthorLoginRequest {
    private String loginId;
    private String password;
}
