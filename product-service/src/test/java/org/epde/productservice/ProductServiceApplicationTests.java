package org.epde.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epde.productservice.dto.ProductRequest;
import org.epde.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    private static final String PRODUCT_API_URL = "/api/product";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.4")
            .withStartupTimeout(Duration.ofSeconds(30));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void cleanRepository() {
        repository.deleteAll();
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 11")
                .description("mobile")
                .price(100.0)
                .build();
    }

    private void performPostProduct(ProductRequest request) throws Exception {
        String productRequestString = objectMapper.writeValueAsString(request);
        mockMvc.perform(post(PRODUCT_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
    }

    private void performGetProductList() throws Exception {
        mockMvc.perform(get(PRODUCT_API_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddProduct() throws Exception {
        ProductRequest request = getProductRequest();
        performPostProduct(request);
        Assertions.assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnProductList() throws Exception {
        Assertions.assertEquals(0, repository.count());

        ProductRequest request = getProductRequest();
        performPostProduct(request);

        mockMvc.perform(get(PRODUCT_API_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(request.getName()))
                .andExpect(jsonPath("$[0].description").value(request.getDescription()))
                .andExpect(jsonPath("$[0].price").value(request.getPrice()));

        Assertions.assertEquals(1, repository.count());
    }
}
