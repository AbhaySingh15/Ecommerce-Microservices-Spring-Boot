package com.abhay.salesorderservice.service;

import com.abhay.salesorderservice.dto.SalesOrderDto;

import java.util.List;


public interface OrderService {

    SalesOrderDto createOrder(SalesOrderDto salesOrderDto);
    List<SalesOrderDto> getOrderDetailsByCustomerId(Long cust_id);
    SalesOrderDto getOrderDetailsByOrderId(Long order_id);
//    ResponseEntity<?> updateOrder(Long orderId, SalesOrderDto salesOrderDtoInput);

    SalesOrderDto deleteOrderByOrderId(Long orderId);
}
