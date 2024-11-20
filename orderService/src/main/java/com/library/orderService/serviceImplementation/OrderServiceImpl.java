package com.library.orderService.serviceImplementation;

import com.library.orderService.dto.OrderDto;
import com.library.orderService.model.OrderBook;
import com.library.orderService.repository.OrderRepository;
import com.library.orderService.service.OrderSevice;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderServiceImpl implements OrderSevice {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${book.service.url}")
    private String bookServiceUrl;

    @Override
    @CircuitBreaker(name = "orderService",fallbackMethod = "fallbackCreateOrder")
    //@Retry(name = "orderServiceRetry",fallbackMethod = "fallbackCreateOrder")
    public OrderBook createOrder(OrderBook orderBook) {


        // Correct URL construction
        String bookServiceUrl = this.bookServiceUrl + "/getBookById/" + orderBook.getBookId();
        logger.info("Calling book service at: {}", bookServiceUrl);

        try {
            // RestTemplate call with proper logging
            ResponseEntity<OrderDto> bookResponse = restTemplate.exchange(
                    bookServiceUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    OrderDto.class
            );


            if (bookResponse.getStatusCode() == HttpStatus.OK && bookResponse.getBody() != null) {
                OrderDto orderDto = bookResponse.getBody();
                logger.info("Received response from book service: {}", orderDto);

                // Check if enough quantity is available
                if (orderDto.getQuantity() >= orderBook.getOrderQuantity()) {
                    logger.info("Sufficient quantity available: {}", orderDto.getQuantity());

                    // Reduce the quantity
                    orderDto.setQuantity(orderDto.getQuantity() - orderBook.getOrderQuantity());

                    // Update the book quantity in the book service
                    String updateBookUrl = this.bookServiceUrl + "/updateBook";
                    restTemplate.exchange(
                            updateBookUrl,
                            HttpMethod.PUT,
                            new HttpEntity<>(orderDto),
                            Void.class
                    );

                    // Place the order
                    orderBook.setOrderStatus("Order Placed");
                    return orderRepository.save(orderBook);
                } else {
                    logger.warn("Insufficient quantity: Available {}, Requested {}", orderDto.getQuantity(), orderBook.getOrderQuantity());
                    orderBook.setOrderStatus("Insufficient Quantity");
                    throw new RuntimeException("Insufficient Quantity");
                }
            } else {
                logger.error("Book not available: Book ID {}", orderBook.getBookId());
                orderBook.setOrderStatus("Book Not Available");
                throw new RuntimeException("Book Not Available");
            }
        }
        catch (Exception e) {
            logger.error("Error calling book service: {}", e.getMessage());
            throw e;  // Let the retry mechanism handle the exception
        }

    }

    public OrderBook fallbackCreateOrder(OrderBook orderBook,Throwable throwable){
        orderBook.setOrderStatus("Book service unavailable. Please try again later.");
        return orderBook;
    }
}



