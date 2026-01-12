package com.avocato.products_catalog.controller;

import com.avocato.products_catalog.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class CatalogController {

    private final List<Product> products = Arrays.asList(
            new Product("Notebook Pro", "electronics", 3500.0),
            new Product("Smartphone X", "electronics", 2500.0),
            new Product("Cafeteira Premium", "home", 400.0),
            new Product("Livro Java", "books", 120.0),
            new Product("Tênis Runner", "sports", 300.0)
    );

    // Endpoint: lista todos os produtos
    @GetMapping("/catalog")
    public List<Product> getCatalog() {
        return products;
    }

    // Endpoint: recomendações por categoria
    @GetMapping("/recommendations")
    public List<Product> getRecommendations(@RequestParam String category) {
        return products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        double avgPrice = products.stream().mapToDouble(Product::getPrice).average().orElse(0);
        Product cheapest = products.stream().min(Comparator.comparingDouble(Product::getPrice)).orElse(null);
        Product mostExpensive = products.stream().max(Comparator.comparingDouble(Product::getPrice)).orElse(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", products.size());
        stats.put("averagePrice", avgPrice);
        stats.put("cheapestItem", cheapest);
        stats.put("mostExpensiveItem", mostExpensive);

        return ResponseEntity.ok(stats);
    }

    // Endpoint: item aleatório
    @GetMapping("/random-item")
    public Product getRandomItem() {
        Random random = new Random();
        return products.get(random.nextInt(products.size()));
    }
}
