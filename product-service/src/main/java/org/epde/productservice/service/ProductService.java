package org.epde.productservice.service;

import org.epde.productservice.dto.ProductRequest;
import org.epde.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    void addProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();

}
