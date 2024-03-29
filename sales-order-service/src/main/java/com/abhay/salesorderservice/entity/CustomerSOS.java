package com.abhay.salesorderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSOS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cust_id;
    private String cust_email;
    private String cust_first_name;
    private String cust_last_name;

}
