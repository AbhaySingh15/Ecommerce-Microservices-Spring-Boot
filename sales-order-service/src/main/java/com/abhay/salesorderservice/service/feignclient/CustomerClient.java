package com.abhay.salesorderservice.service.feignclient;

import com.abhay.salesorderservice.entity.CustomerSOS;
import jakarta.ws.rs.Path;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customer/{id}")
    CustomerSOS getCustomerById(@PathVariable Long id);
}
