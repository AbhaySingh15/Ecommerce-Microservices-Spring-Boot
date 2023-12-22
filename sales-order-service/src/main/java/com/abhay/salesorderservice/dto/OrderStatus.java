package com.abhay.salesorderservice.dto;

import com.abhay.salesorderservice.enums.OrderStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatus {
    private int statusCode;
    private String desc;

    public OrderStatus(OrderStatusEnum orderStatusEnum){
        this.statusCode = orderStatusEnum.getCode();
        this.desc = orderStatusEnum.getMsg();
    }
}
