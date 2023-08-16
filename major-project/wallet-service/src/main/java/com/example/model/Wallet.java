package com.example.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true)

    private String walletId; //This one is User Id that we are consuming from the kafka

    private Long balance;

    private String currency;

    @CreationTimestamp
    private Date CreatedOn;

    @UpdateTimestamp
    private Date UpdatedOn;
}
