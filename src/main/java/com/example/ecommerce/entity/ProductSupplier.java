package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductSupplier {
    private Long productId;
    private Long supplierId;
    private BigDecimal supplyPrice;
}
