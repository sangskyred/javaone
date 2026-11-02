package com.example.ecommerce;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductVariant;
import com.example.ecommerce.dto.request.CartItemRequest;
import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.RegisterRequest;
import com.example.ecommerce.dto.response.AccountResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductVariantRepository;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class EcommerceApplicationTests {
    @Autowired
	private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

	@Autowired
	private AuthService authService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Test
	void contextLoads() {
	}

	@Test
	void productMapperSupportsCrud() {
		Product product = new Product();
		product.setName("Test product");
		product.setDescription("Created in integration test");

		assertEquals(1, productRepository.insert(product));
		assertNotNull(product.getId());

        ProductVariant variant = new ProductVariant();
        variant.setProductId(product.getId());
        variant.setSku("TEST-PRODUCT-1");
        variant.setAttributes(java.util.Collections.emptyMap());
        variant.setPrice(new BigDecimal("12.50"));
        variant.setStock(4);
        variant.setActive(true);
        assertEquals(1, productVariantRepository.insert(variant));
		assertEquals("Test product", productRepository.findById(product.getId()).getName());

		product.setName("Updated product");
		assertEquals(1, productRepository.update(product));
		assertEquals("Updated product", productRepository.findById(product.getId()).getName());
		assertEquals(1, productRepository.deleteById(product.getId()));
	}

    @Test
    void customerCanRegisterLoginAndCheckoutCart() {
	AccountResponse account = authService.register(
		new RegisterRequest("buyer@example.com", "secure-pass-123", "Test Buyer"));
	assertEquals(account.id(), authService.login(
		new LoginRequest("buyer@example.com", "secure-pass-123")).id());

	Product product = new Product();
	product.setName("Order test product");
	product.setDescription("Stock must be reduced on checkout");
	productRepository.insert(product);

    ProductVariant variant = new ProductVariant();
    variant.setProductId(product.getId());
    variant.setSku("ORDER-TEST-PRODUCT-1");
    variant.setAttributes(java.util.Collections.emptyMap());
    variant.setPrice(new BigDecimal("6.25"));
    variant.setStock(4);
    variant.setActive(true);
    productVariantRepository.insert(variant);

	cartService.addItem(account.id(), new CartItemRequest(product.getId(), 2));
	OrderResponse order = orderService.checkout(account.id());

	assertEquals(new BigDecimal("12.50"), order.total());
	assertEquals("pending", order.status());
	assertEquals(1, order.items().size());
	assertEquals(2, order.items().getFirst().quantity());
	assertEquals(2, productVariantRepository.findById(variant.getId()).getStock());
	assertEquals(0, cartService.getCart(account.id()).items().size());
    }
}