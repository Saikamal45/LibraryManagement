package com.library.orderService.serviceImplementation;

import com.library.orderService.dto.OrderDto;
import com.library.orderService.model.OrderBook;
import com.library.orderService.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;


@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {

       OrderBook orderBook = new OrderBook();
        orderBook.setBookId(1);
        orderBook.setOrderQuantity(5);

        OrderDto orderDto = new OrderDto();
        orderDto.setQuantity(10);
    }
}

