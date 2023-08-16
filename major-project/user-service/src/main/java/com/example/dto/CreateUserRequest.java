package com.example.dto;

import com.example.Model.User;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUserRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String phone_number;
    private String email;
    @Min(18)
    private Integer age;

    public User to()
    {
        return User.builder().name(this.name).phone_number(this.phone_number).age(this.age).email(this.email).build();
    }
}
