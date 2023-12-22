package com.abhay.itemservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Categories category;
    // @JsonIgnore is important here, otherwise stackoverflow error
    // will be caused during serialization
    @OneToOne
    @JsonIgnore
    private Item item;
}
