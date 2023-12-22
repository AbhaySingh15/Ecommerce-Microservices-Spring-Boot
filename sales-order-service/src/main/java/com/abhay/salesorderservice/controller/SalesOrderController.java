package com.abhay.salesorderservice.controller;

import com.abhay.salesorderservice.dto.SalesOrderDto;
import com.abhay.salesorderservice.enums.ResultEnum;
import com.abhay.salesorderservice.exception.MyException;
import com.abhay.salesorderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class SalesOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * @param salesOrderDto  Order request object from client
     * @return SalesOrderDto - Returns DTO with order details
     */
    @PostMapping("/orders")
    @CircuitBreaker(name="createOrderCircuit", fallbackMethod = "fallbackItemServiceDown")
    public SalesOrderDto createOrder(@RequestBody @Valid SalesOrderDto salesOrderDto) {
        return orderService.createOrder(salesOrderDto);
    }

    /**
     * @param cust_id
     * @return ResponseEntity<List<SalesOrderDto>>
     */
    @GetMapping("/orders")
    public ResponseEntity<List<SalesOrderDto>> getAllOrdersOfACustomer(@RequestParam("customerId") Long cust_id) {
//        ResponseEntity<?> allOrdersOfACustomer = getAllOrdersOfACustomer(cust_id);
//        for(SalesOrderDto salesOrderDto: allOrdersOfACustomer){
//            Link selfLink = linkTo(SalesOrderController.class).slash("/orders?").slash("customerId="+cust_id).withSelfRel();
//            salesOrderDto.add(selfLink);
//        }
//        return orderService.getOrderDetailsByCustomerId(cust_id);
        List<SalesOrderDto> salesOrderDtoList = orderService.getOrderDetailsByCustomerId(cust_id);
        return salesOrderDtoList.isEmpty()?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(salesOrderDtoList):
                ResponseEntity.ok(salesOrderDtoList);
    }

    /**
     * @param orderId
     * @return ResponseEntity<SalesOrderDto>
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<SalesOrderDto> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        SalesOrderDto salesOrderDto = orderService.getOrderDetailsByOrderId(orderId);
        return !salesOrderDto.toString().isEmpty() ?
                ResponseEntity.ok(salesOrderDto):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(salesOrderDto);
    }

//    @PutMapping("/order/{orderId}")
//    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody SalesOrderDto salesOrderDtoInput) {
//        return orderService.updateOrder(orderId,salesOrderDtoInput);
//    }

    /**
     * @param orderId
     * @return ResponseEntity<SalesOrderDto>
     */
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<SalesOrderDto> deleteOrderByOrderId(@PathVariable Long orderId){
        SalesOrderDto salesOrderDto = orderService.deleteOrderByOrderId(orderId);
        return salesOrderDto!=null?
                ResponseEntity.ok(salesOrderDto) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }

    /**
     * @param throwable
     * @return
     */
    public SalesOrderDto fallbackItemServiceDown(Throwable throwable){
        SalesOrderDto salesOrderDto = new SalesOrderDto();
        salesOrderDto.setOrder_desc("item service down, order cannot be placed");
        return salesOrderDto;
    }
}
