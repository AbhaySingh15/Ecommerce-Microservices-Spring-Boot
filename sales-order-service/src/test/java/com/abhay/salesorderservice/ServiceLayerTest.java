package com.abhay.salesorderservice;

import com.abhay.salesorderservice.dto.SalesOrderDto;
import com.abhay.salesorderservice.entity.CustomerSOS;
import com.abhay.salesorderservice.entity.Item;
import com.abhay.salesorderservice.entity.SalesOrder;
import com.abhay.salesorderservice.enums.OrderStatusEnum;
import com.abhay.salesorderservice.enums.ResultEnum;
import com.abhay.salesorderservice.repository.Customer_SOS_Repository;
import com.abhay.salesorderservice.repository.OrderRepository;
import com.abhay.salesorderservice.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class ServiceLayerTest {

    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private Customer_SOS_Repository customer_sos_repository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private ModelMapper modelMapper;
    @Autowired
    @InjectMocks
    private OrderServiceImpl orderService;

    // createOrder method successfully creates an order with valid input
    @Test
    public void test_createOrder_success(){
        Item[] items = new Item[]{createItem("Macbook pro 16 inch","Apple macbook",1500.00),
                                    createItem("Realme buds wireless 3","wireless earphones",40.00),
                                     createItem("levis’s blue T-shirt","man’s top wear",25.00)};
        CustomerSOS customerSOS = getCustomerSOS();

        when(restTemplate.getForEntity(anyString(), eq(Item[].class))).thenReturn(new ResponseEntity<Item[]>(items,HttpStatus.OK));
        when(customer_sos_repository.findById(anyLong())).thenReturn(Optional.of(customerSOS));
        when(orderRepository.save(any(SalesOrder.class))).thenReturn(new SalesOrder());

        Date date = new Date();
        SalesOrderDto salesOrderDtoInput = new SalesOrderDto();
        salesOrderDtoInput.setCust_id(1L);
        salesOrderDtoInput.setItem_names(Arrays.asList("item1", "item2"));
        salesOrderDtoInput.setOrder_desc("test order");
        log.info(date.toString());
        salesOrderDtoInput.setOrder_date(date);

        SalesOrderDto result = orderService.createOrder(salesOrderDtoInput);
        log.info(result.getOrderStatus().getDesc());

        assertNotNull(result);
        assertEquals(OrderStatusEnum.ORDER_PLACED.getCode(), result.getOrderStatus().getStatusCode());
    }

    // createOrder method handles empty item_names list

    @Test
    public void test_createOrder_forUnavailableItems() {
        CustomerSOS customerSOS = getCustomerSOS();

        when(restTemplate.getForEntity(anyString(), eq(Item[].class))).thenReturn(new ResponseEntity<Item[]>(new Item[0],HttpStatus.OK));
        when(customer_sos_repository.findById(anyLong())).thenReturn(Optional.of(customerSOS));

        Date date = new Date();
        SalesOrderDto salesOrderDtoInput = new SalesOrderDto();

        salesOrderDtoInput.setOrder_desc("test order");
        log.info(date.toString());
        salesOrderDtoInput.setOrder_date(date);
        salesOrderDtoInput.setCust_id(1L);
        salesOrderDtoInput.setItem_names(List.of("item1","item2"));
        SalesOrderDto result = orderService.createOrder(salesOrderDtoInput);

        log.info(result.getOrderStatus().getDesc());

        assertNotNull(result);
        assertEquals(ResultEnum.NO_ITEM_IN_STOCK.toString(), result.getOrderStatus().getDesc());

    }
    // createOrder method should not place order when customerId is invalid
    // and should send customer not found enum in order status desc
    @Test
    public void test_createOrder_invalidCustomerId() {
        Item[] items = new Item[]{createItem("Macbook pro 16 inch","Apple macbook",1500.00)};
//        CustomerSOS customerSOS = getCustomerSOS();

        when(restTemplate.getForEntity(anyString(), eq(Item[].class))).thenReturn(new ResponseEntity<Item[]>(items,HttpStatus.OK));
        when(customer_sos_repository.findById(anyLong())).thenReturn(Optional.empty());

        Date date = new Date();

        SalesOrderDto salesOrderDtoInput = new SalesOrderDto();
        salesOrderDtoInput.setOrder_desc("test order");
        log.info(date.toString());
        salesOrderDtoInput.setOrder_date(date);
        salesOrderDtoInput.setCust_id(1L);
        salesOrderDtoInput.setItem_names(Arrays.asList("item1", "item2"));
        SalesOrderDto result = orderService.createOrder(salesOrderDtoInput);

        log.info(result.getOrderStatus().getDesc());

        assertNotNull(result);
        assertEquals(ResultEnum.CUSTOMER_NOT_FOUND.toString(), result.getOrderStatus().getDesc());
    }

    private Item createItem(String name, String description, double price) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);

        return item;
    }

    private static CustomerSOS getCustomerSOS() {
        CustomerSOS customerSOS = new CustomerSOS();
        customerSOS.setCust_id(1L);
        customerSOS.setCust_first_name("john");
        customerSOS.setCust_last_name("doe");
        customerSOS.setCust_email("johndoe@email.com");
        return customerSOS;
    }

//    @Test
//    public void testGetOrderDetailsByOrderID(){
////      when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(new SalesOrder()));
////      SalesOrderDto salesOrderDto = new SalesOrderDto();
////      salesOrderDto.setId(1L);
////      when(modelMapper.map(any(SalesOrder.class),SalesOrderDto.class)).thenReturn(salesOrderDto);
////      assertNotNull(orderService.getOrderDetailsByOrderId(1L).getId());
//
//        SalesOrder salesOrder = new SalesOrder();
//        salesOrder.setId(1L);
//
//        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(salesOrder));
//        SalesOrderDto salesOrderDto = orderService.getOrderDetailsByOrderId(1L);
//        log.info(salesOrderDto.getId().toString());
////        assertNotNull(orderService.getOrderDetailsByOrderId(1L).getId());
//
//    }
}

