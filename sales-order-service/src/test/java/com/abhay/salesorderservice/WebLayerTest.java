package com.abhay.salesorderservice;

import com.abhay.salesorderservice.dto.SalesOrderDto;
import com.abhay.salesorderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class WebLayerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreateOrderController() throws Exception {

//        ResponseEntity responseEntity = ResponseEntity.ok("Order created");
        //Mocking OrderService createOrder() method
        when(orderService.createOrder(any(SalesOrderDto.class))).thenReturn(new SalesOrderDto());

        SalesOrderDto salesOrderDto = getSalesOrderDto();

        //Converting salesOrderDto to JSON
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String salesOrderDtoJson = gson.toJson(salesOrderDto);

        //Performing mock request and testing valid input
        //Should return ok status
        mockMvc.perform(post("/orders").content(objectMapper.writeValueAsString(salesOrderDto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testCreateOrderNegative() throws Exception{
        when(orderService.createOrder(any(SalesOrderDto.class))).thenReturn(new SalesOrderDto());

        SalesOrderDto salesOrderDto = getSalesOrderDto();
        salesOrderDto.setCust_id(null);
        salesOrderDto.setItem_names(null);
        //Converting salesOrderDto to JSON
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String salesOrderDtoJson = gson.toJson(salesOrderDto);

        //Performing mock request and testing invalid input
        //Should return bad request
        mockMvc.perform(post("/orders").content(objectMapper.writeValueAsString(salesOrderDto)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    private static SalesOrderDto getSalesOrderDto() {
        //creating test salesOrderDto object
        SalesOrderDto salesOrderDto = new SalesOrderDto();
        salesOrderDto.setCust_id(1L);
        salesOrderDto.setOrder_desc("testing sales order dto");
        List<String> itemList = List.of("Laptop","Monitor");
        salesOrderDto.setItem_names(itemList);
        return salesOrderDto;
    }

    @Test
    public void testGetAllOrdersOfCustomer() throws Exception {
        when(orderService.getOrderDetailsByCustomerId(any(Long.class))).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/orders").param("customerId","1")).andDo(print()).andExpect(content().string("[]"));
    }

    @Test
    public void testGetAllOrdersOfCustomerNegative() throws Exception {
        when(orderService.getOrderDetailsByCustomerId(any(Long.class))).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/orders").param("custId","1")).andDo(print()).andExpect(status().isBadRequest());
//        mockMvc.perform(get("/orders?CustomeId=1")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOrderDetailsByOrderId() throws Exception {
        when(orderService.getOrderDetailsByOrderId(any(Long.class))).thenReturn(new SalesOrderDto());
        mockMvc.perform(get("/order/{orderId}",1L)).andDo(print()).andExpect(jsonPath("$").exists());
    }

}
