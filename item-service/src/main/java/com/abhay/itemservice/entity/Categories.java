package com.abhay.itemservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;


@Entity
@Data
@NoArgsConstructor
public class Categories {
    @Id
    @GeneratedValue
    private int category_id;
    private String name ;
    private int category_level;
    @ManyToOne
    private Categories parent_category;
}
