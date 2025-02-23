package org.epde.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.epde.productservice.dto.ProductRequest;
import org.epde.productservice.dto.ProductResponse;
import org.epde.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addProduct(@RequestBody ProductRequest productRequest) {
        productService.addProduct(productRequest);
        return "Product added successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
