package com.abhay.salesorderservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrder extends RepresentationModel<SalesOrder> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date order_date;
    private String order_desc;
    private double total_price;

    @ManyToOne
    private CustomerSOS customer_sos;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "salesOrder")
    private List<Order_Line_Item> order_line_itemList;

    public void setOrderLineItemList(List<Order_Line_Item> updatedOrderLineItemList){
        this.order_line_itemList.clear();
        this.order_line_itemList.addAll(updatedOrderLineItemList);
    }
}
