package com.avocato.products_catalog.controller;

import com.avocato.products_catalog.domain.Product;
import com.avocato.products_catalog.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Catalog Controller", description = "Gerenciamento de produtos e estatisticas")
public class CatalogController {

    @Autowired
    private ProductRepository productRepository;

    // Endpoint: lista todos os produtos
    @GetMapping("/catalog")
    @Operation(
            summary = "Listar todos os produtos",
            description = "Retorna o catálogo completo de produtos disponíveis"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Catálogo retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<List<Product>> getCatalog() {
        return ResponseEntity.ok(productRepository.findAll().stream().map(productEntity ->
                new Product(productEntity.getName(), productEntity.getCategory(), productEntity.getPrice())
        ).toList());
    }

    // Endpoint: recomendações por categoria
    @GetMapping("/recommendations")
    @Operation(
            summary = "Obter recomendações por categoria",
            description = "Retorna uma lista de produtos filtrados por categoria específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recomendações retornadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro de categoria inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<List<Product>>  getRecommendations(@RequestParam String category) {
        return ResponseEntity.ok(productRepository.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .map(productEntity -> new Product(productEntity.getName(), productEntity.getCategory(), productEntity.getPrice()))
                .toList());
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Obter estatísticas dos produtos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estatísticas calculadas com sucesso"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Map<String, Object>> getStats() {
        var products = productRepository.findAll().stream()
                .map(productEntity -> new Product(productEntity.getName(), productEntity.getCategory(), productEntity.getPrice()))
                .toList();

        Double avgPrice = products.stream().mapToDouble(product -> product.getPrice().doubleValue()).average().orElse(0);
        Product cheapest = products.stream().min(Comparator.comparingDouble(product -> product.getPrice().doubleValue())).orElse(null);
        Product mostExpensive = products.stream().max(Comparator.comparingDouble(product -> product.getPrice().doubleValue())).orElse(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", products.size());
        stats.put("averagePrice", avgPrice);
        stats.put("cheapestItem", cheapest);
        stats.put("mostExpensiveItem", mostExpensive);

        return ResponseEntity.ok(stats);
    }
}
