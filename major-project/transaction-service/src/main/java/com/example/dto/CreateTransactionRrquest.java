package com.example.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CreateTransactionRrquest {
    //Here we are using the phn number as ID's
    @NotBlank
    private String receiver;
    @NotBlank
    private String sender;//sender id

    @Min(1)
    private Long amount; //lowest Denomination
    private String reason;

}
