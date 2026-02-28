package com.avocato.products_catalog.domain;

import java.math.BigDecimal;

public class Product {

    private String name;
    private String category;
    private BigDecimal price;

    public Product(String name, String category, BigDecimal price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return name.equals(product.name) &&
               category.equals(product.category) &&
               price.equals(product.price);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + category.hashCode();
        temp = Double.doubleToLongBits(price.doubleValue());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
