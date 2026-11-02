package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Currency {
    private String code;
    private String name;
    private BigDecimal exchangeRate;
    private OffsetDateTime updatedAt;
}
