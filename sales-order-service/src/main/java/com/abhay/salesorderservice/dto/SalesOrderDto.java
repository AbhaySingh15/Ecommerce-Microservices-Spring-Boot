package com.abhay.salesorderservice.dto;

import com.abhay.salesorderservice.entity.Order_Line_Item;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.hateoas.RepresentationModel;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SalesOrderDto extends RepresentationModel<SalesOrderDto> {

    private String order_desc;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date order_date;
    @NotNull(message = "customer id cannot be null")
    private Long cust_id;
    @NotEmpty(message = "item list cannot be empty")
    private List<String> item_names;
    private Long id;
    private List<Order_Line_Item> order_line_itemList;
    private double total_price;
    private OrderStatus orderStatus;
}
