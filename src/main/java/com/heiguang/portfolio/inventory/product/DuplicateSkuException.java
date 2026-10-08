package com.heiguang.portfolio.inventory.product;

public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String sku) {
        super("SKU '%s' already exists".formatted(sku));
    }
}
