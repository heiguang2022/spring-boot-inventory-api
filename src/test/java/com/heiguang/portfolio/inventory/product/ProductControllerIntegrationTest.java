package com.heiguang.portfolio.inventory.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsProductAndNormalizesSku() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateProductRequest(
                                " tsh_001 ", "Classic T-Shirt", new BigDecimal("19.99"), 25
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("TSH-001"))
                .andExpect(jsonPath("$.quantity").value(25));
    }

    @Test
    void rejectsDuplicateSkuIgnoringCaseAndSeparators() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateProductRequest(
                                "SKU_001", "Original", new BigDecimal("9.99"), 1
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateProductRequest(
                                "sku-001", "Duplicate", new BigDecimal("9.99"), 1
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("SKU 'SKU-001' already exists"));
    }

    @Test
    void rejectsInvalidProductFields() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"bad sku","name":"","price":0,"quantity":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.sku").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.price").exists())
                .andExpect(jsonPath("$.validationErrors.quantity").exists());
    }

    @Test
    void updatesInventoryAndIncrementsVersion() throws Exception {
        Product product = createProduct("BOT-002");

        mockMvc.perform(patch("/api/products/{id}/inventory", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":42}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(42))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void returnsStructuredNotFoundError() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/products/9999"));
    }

    @Test
    void listsAndDeletesProducts() throws Exception {
        Product product = createProduct("BAG-004");

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }

    private Product createProduct(String sku) {
        return repository.save(new Product(sku, "Demo Product", new BigDecimal("19.99"), 10));
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
