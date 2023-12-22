package com.abhay.salesorderservice.service.impl;

import com.abhay.salesorderservice.dto.OrderStatus;
import com.abhay.salesorderservice.dto.SalesOrderDto;
import com.abhay.salesorderservice.entity.CustomerSOS;
import com.abhay.salesorderservice.entity.Item;
import com.abhay.salesorderservice.entity.Order_Line_Item;
import com.abhay.salesorderservice.entity.SalesOrder;
import com.abhay.salesorderservice.enums.OrderStatusEnum;
import com.abhay.salesorderservice.enums.ResultEnum;
import com.abhay.salesorderservice.exception.MyException;
import com.abhay.salesorderservice.repository.Customer_SOS_Repository;
import com.abhay.salesorderservice.repository.OrderRepository;
import com.abhay.salesorderservice.service.OrderService;
import com.abhay.salesorderservice.service.feignclient.CustomerClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private Customer_SOS_Repository customer_sos_repository;
    @Autowired
    private RestTemplate restTemplate;
//    @Autowired
//    private CustomerClient customerClient;

    /**
     * @param salesOrderDtoInput  Order request object from client
     * @return SalesOrderDto - DTO of sales order created
     */
    @Override
    public SalesOrderDto createOrder(SalesOrderDto salesOrderDtoInput) {
        Optional<CustomerSOS> customer = customer_sos_repository.findById(salesOrderDtoInput.getCust_id());
//        CustomerSOS customer = customerClient.getCustomerById(salesOrderDtoInput.getCust_id());
        Item[] itemArray = getItemsFromItemService(salesOrderDtoInput.getItem_names());
        try {
            checksBeforePlacingOrder(customer, itemArray);
        } catch (MyException exception) {
            return handleCreateOrderException(salesOrderDtoInput, exception.getMessage());
        } catch (Exception exception) {
           return handleCreateOrderException(salesOrderDtoInput,"Sorry order not placed due to some backend issue");
        }
        double totalPriceOfOrder = getOrderPrice(itemArray);
        // mapping salesOrder object from salesOrderRequestModel object
        SalesOrder salesOrder = modelMapper.map(salesOrderDtoInput, SalesOrder.class);
        // setting customer id for the order object
        salesOrder.setCustomer_sos(customer.get());
        log.info(customer.get().getCust_first_name());
        salesOrder.setTotal_price(totalPriceOfOrder);
//        log.info(salesOrder.toString());
        salesOrder.setOrder_line_itemList(createOrderLineItemList(itemArray, salesOrder));
        orderRepository.save(salesOrder);

        SalesOrderDto salesOrderDto = modelMapper.map(salesOrder, SalesOrderDto.class);
        OrderStatus orderStatus = new OrderStatus(OrderStatusEnum.ORDER_PLACED);
        salesOrderDto.setOrderStatus(orderStatus);
        return salesOrderDto;
    }

//    private void checksBeforePlacingOrder(Optional<CustomerSOS> customer, Item[] itemArray) throws MyException {
//        if (customer.isEmpty()) {
//            throw new MyException(ResultEnum.CUSTOMER_NOT_FOUND);
//        } else {
//            if (ArrayUtils.isEmpty(itemArray)) {
//                throw new MyException(ResultEnum.NO_ITEM_IN_STOCK);
//            }
//        }
//    }

    /**
     * @param customer  Optional customer object
     * @param itemArray  itemArray containing list of requested items
     * @throws MyException - Exception stating why order is not created
     */
    private void checksBeforePlacingOrder(Optional<CustomerSOS> customer, Item[] itemArray) throws MyException {
        if (customer.isEmpty()) {
            throw new MyException(ResultEnum.CUSTOMER_NOT_FOUND);
        } else {
            if (ArrayUtils.isEmpty(itemArray)) {
                throw new MyException(ResultEnum.NO_ITEM_IN_STOCK);
            }
        }
    }

    /**
     * @param salesOrderDtoInput  Object request object from client
     * @param exceptionMessage  Exception message explaining why order is not created
     * @return SalesOrderDto - Sales order dto with status not placed and reason for it
     */
    private SalesOrderDto handleCreateOrderException(SalesOrderDto salesOrderDtoInput, String exceptionMessage) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatusCode(OrderStatusEnum.ORDER_NOT_PLACED.getCode());
        orderStatus.setDesc(exceptionMessage);
        salesOrderDtoInput.setOrderStatus(orderStatus);
        return salesOrderDtoInput;
    }

    /**
     * @param itemArray  Item array containing list of requested items
     * @return double - Total price of order
     */
    private double getOrderPrice(Item[] itemArray){
        return Arrays.stream(itemArray).mapToDouble(Item::getPrice).sum();
    }

    /**
     * @param cust_id  Customer ID
     * @return List<SalesOrderDto> - List of all sales order of a customer
     */
    @Override
    public List<SalesOrderDto> getOrderDetailsByCustomerId(Long cust_id) {
        List<SalesOrder> salesOrder = orderRepository.findByCustomerId(cust_id);
        List<SalesOrderDto> salesOrderDto = salesOrder.stream()
                .map(order -> modelMapper.map(order, SalesOrderDto.class))
                .collect(Collectors.toList());
        log.info(salesOrderDto.toString());
        return salesOrderDto;
    }

    /**
     * @param order_id  Order ID of order whose details are requested
     * @return SalesOrderDTO - DTO with details of requested order
     */
    @Override
    public SalesOrderDto getOrderDetailsByOrderId(Long order_id) {
        Optional<SalesOrder> salesOrderEntity = orderRepository.findById(order_id);
        if (salesOrderEntity.isPresent()) {
            SalesOrder salesOrder = salesOrderEntity.get();
            return modelMapper.map(salesOrder, SalesOrderDto.class);
        }
        return new SalesOrderDto();
    }

    /**
     * @param itemNameList  List containing requested items
     * @return Item[] - Array containing items requested from item service
     */
    public Item[] getItemsFromItemService(List<String> itemNameList) {
        StringBuilder getItemsByNameUrl = new StringBuilder("http://item-service/item?name=");
        String itemsRequestedByCustomer = String.join(",", itemNameList);
        log.info("Line "+getLineNumber()+": "+itemsRequestedByCustomer);
        getItemsByNameUrl.append(itemsRequestedByCustomer);
        log.info("Line "+getLineNumber()+": "+getItemsByNameUrl.toString());
        ResponseEntity<Item[]> responseEntity = restTemplate.getForEntity(getItemsByNameUrl.toString(), Item[].class);
        // getting the item array containing all items from item service
        Item[] itemArray = responseEntity.getBody();
        log.info(Arrays.stream(itemArray).map(Item::getName).toList().toString());
        if (ArrayUtils.isEmpty(itemArray)) {
            log.info("order cannot be placed as not a single item requested by customer is in the stock");
            return null;
        } else {
            return itemArray;
        }
    }

    /**
     * @param orderId  ID of the order to be deleted
     * @return SalesOrderDto - DTO of SalesOrder deleted
     */
    @Override
    @Transactional
    public SalesOrderDto deleteOrderByOrderId(Long orderId) {
        Optional<SalesOrder> optionalSalesOrder = orderRepository.findById(orderId);
        if (optionalSalesOrder.isPresent()) {
            SalesOrderDto salesOrderDto = modelMapper.map(optionalSalesOrder,SalesOrderDto.class);
            OrderStatus orderStatus = new OrderStatus(OrderStatusEnum.ORDER_DELETED);
            salesOrderDto.setOrderStatus(orderStatus);
            // order line items will get automatically deleted
            // because of cascade.remove operation
            orderRepository.deleteById(orderId);
            log.info(salesOrderDto.toString());
            return salesOrderDto;
        } else {
             return null;
        }
    }

    /**
     * @param items  Items to create Order Line Item List
     * @param salesOrder  Sales order to set in each order line item
     * @return List<Order_Line_Item> - List of order line item
     */
    public List<Order_Line_Item> createOrderLineItemList(Item[] items, SalesOrder salesOrder) {
        List<Order_Line_Item> order_line_itemList = new ArrayList<>();
        for (Item item : items) {
            Order_Line_Item order_line_item = new Order_Line_Item();
            order_line_item.setName(item.getName());
            order_line_item.setSalesOrder(salesOrder);
//            log.info("Line "+getLineNumber()+": "+order_line_item.getName());
            order_line_itemList.add(order_line_item);
        }
        return order_line_itemList;
    }
    private int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }
}
