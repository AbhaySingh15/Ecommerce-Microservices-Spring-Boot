package com.abhay.itemservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.*;


@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item extends RepresentationModel<Item> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "item name cannot be empty")
    private String name;
    private String description;
    @DecimalMin(value = "1.0")
    private double price;
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true,mappedBy = "item")
    private ItemCategory itemCategory;

    @Transient
    @Min(value = 1)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int categoryId;
}
