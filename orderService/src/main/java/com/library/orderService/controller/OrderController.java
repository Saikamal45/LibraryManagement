package com.library.orderService.controller;

import com.library.orderService.model.OrderBook;
import com.library.orderService.service.OrderSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderSevice orderSevice;

    @PostMapping("/orderBook")
    public ResponseEntity<OrderBook> createOrder(@RequestBody OrderBook orderBook) {
        try {
            // Attempt to create the order
            OrderBook createdOrder = orderSevice.createOrder(orderBook);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Set the order status based on the exception message
            if ("Insufficient Quantity".equals(e.getMessage())) {
                orderBook.setOrderStatus("Insufficient Quantity");
                return new ResponseEntity<>(orderBook, HttpStatus.BAD_REQUEST);
            } else if ("Book Not Available".equals(e.getMessage())) {
                orderBook.setOrderStatus("Book Not Available");
                return new ResponseEntity<>(orderBook, HttpStatus.NOT_FOUND);
            } else {
                // Log the unexpected error for debugging
                orderBook.setOrderStatus("Error Processing Order");
                return new ResponseEntity<>(orderBook, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
