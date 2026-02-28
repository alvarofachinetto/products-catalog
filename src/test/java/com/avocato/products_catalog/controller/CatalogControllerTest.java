package com.avocato.products_catalog.controller;

import com.avocato.products_catalog.domain.Product;
import com.avocato.products_catalog.entity.ProductEntity;
import com.avocato.products_catalog.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RestController
public class CatalogControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CatalogController catalogController;

    private List<ProductEntity> mockProducts;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockProducts = createMockProducts();
    }


    private List<ProductEntity> createMockProducts() {
        ProductEntity p1 = new ProductEntity();
        p1.setName("Laptop");
        p1.setCategory("Electronics");
        p1.setPrice(BigDecimal.valueOf(1200.00));

        ProductEntity p2 = new ProductEntity();
        p2.setName("Mouse");
        p2.setCategory("Electronics");
        p2.setPrice(BigDecimal.valueOf(25.00));

        ProductEntity p3 = new ProductEntity();
        p3.setName("Desk");
        p3.setCategory("Furniture");
        p3.setPrice(BigDecimal.valueOf(350.00));

        ProductEntity p4 = new ProductEntity();
        p4.setName("Chair");
        p4.setCategory("Furniture");
        p4.setPrice(BigDecimal.valueOf(150.00));

        return Arrays.asList(p1, p2, p3, p4);
    }

    @Test
    @DisplayName("Test getCatalog returns all products")
    void testGetCatalog() {
        // Implement test logic here
        when(productRepository.findAll()).thenReturn(createMockProducts());

        ResponseEntity<List<Product>> response = catalogController.getCatalog();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar produtos filtrados por categoria")
    void testGetRecommendations_Success() {
        when(productRepository.findAll()).thenReturn(mockProducts);

        ResponseEntity<List<Product>> response = catalogController.getRecommendations("Electronics");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        response.getBody().forEach(product ->
                assertEquals("Electronics", product.getCategory())
        );

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve calcular estatísticas com apenas um produto")
    void testGetStats_SingleProduct() {
        ProductEntity singleProduct = mockProducts.get(0);
        when(productRepository.findAll()).thenReturn(Collections.singletonList(singleProduct));

        ResponseEntity<Map<String, Object>> response = catalogController.getStats();
        Map<String, Object> stats = response.getBody();

        assertEquals(1, stats.get("totalItems"));
        assertEquals(1200.0, (Double) stats.get("averagePrice"), 0.01);

        Product cheapest = (Product) stats.get("cheapestItem");
        Product mostExpensive = (Product) stats.get("mostExpensiveItem");

        assertEquals(cheapest.getName(), mostExpensive.getName());
        assertEquals(cheapest.getPrice(), mostExpensive.getPrice());
    }

    @Test
    @DisplayName("Deve calcular preço médio com precisão decimal")
    void testGetStats_AveragePricePrecision() {
        ProductEntity p1 = new ProductEntity();
        p1.setName("Product1");
        p1.setCategory("Test");
        p1.setPrice(BigDecimal.valueOf(10.99));

        ProductEntity p2 = new ProductEntity();
        p2.setName("Product2");
        p2.setCategory("Test");
        p2.setPrice(BigDecimal.valueOf(20.50));

        ProductEntity p3 = new ProductEntity();
        p3.setName("Product3");
        p3.setCategory("Test");
        p3.setPrice(BigDecimal.valueOf(15.75));

        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        ResponseEntity<Map<String, Object>> response = catalogController.getStats();
        Map<String, Object> stats = response.getBody();

        // Média: (10.99 + 20.50 + 15.75) / 3 = 15.746666...
        assertEquals(15.746666666666666, (Double) stats.get("averagePrice"), 0.0001);
    }

}
