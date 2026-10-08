package com.heiguang.portfolio.inventory.product;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        String normalizedSku = normalizeSku(request.sku());
        if (repository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new DuplicateSkuException(normalizedSku);
        }

        Product product = new Product(
                normalizedSku,
                request.name().trim(),
                request.price(),
                request.quantity()
        );

        try {
            return ProductResponse.from(repository.saveAndFlush(product));
        } catch (DataIntegrityViolationException exception) {
            // 数据库唯一约束负责兜底，避免并发请求绕过应用层重复检查。
            throw new DuplicateSkuException(normalizedSku);
        }
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return repository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse updateInventory(Long id, UpdateInventoryRequest request) {
        Product product = findProduct(id);
        // 实体版本字段提供乐观锁保护，防止并发库存更新静默覆盖。
        product.setQuantity(request.quantity());
        return ProductResponse.from(repository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        repository.delete(product);
    }

    private Product findProduct(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase(Locale.ROOT).replace('_', '-');
    }
}
