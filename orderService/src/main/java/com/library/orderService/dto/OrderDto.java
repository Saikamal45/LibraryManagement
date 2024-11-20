package com.library.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderDto {
    private int id;
    private String bName;
    private String author;
    private int quantity;
}
