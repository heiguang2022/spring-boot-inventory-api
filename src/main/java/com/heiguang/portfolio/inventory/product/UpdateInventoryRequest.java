package com.heiguang.portfolio.inventory.product;

import jakarta.validation.constraints.Min;

public record UpdateInventoryRequest(@Min(0) int quantity) {
}
