package com.heiguang.portfolio.inventory.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "\\s*[A-Za-z0-9][A-Za-z0-9_-]*\\s*", message = "must contain only letters, numbers, hyphens, or underscores")
        String sku,

        @NotBlank
        @Size(max = 160)
        String name,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal price,

        @Min(0)
        int quantity
) {
}
