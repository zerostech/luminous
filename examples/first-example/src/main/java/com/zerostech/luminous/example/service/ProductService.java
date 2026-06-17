/*
 *
 *  *  Copyright 2024. the original author or authors
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 *
 */

package com.zerostech.luminous.example.service;

import com.zerostech.luminous.example.dto.ProductCreateRequest;
import com.zerostech.luminous.example.dto.ProductUpdateRequest;
import com.zerostech.luminous.example.model.Product;

import java.util.List;

/**
 * Product service interface.
 */
public interface ProductService {

    /**
     * Create a new product.
     */
    Product create(ProductCreateRequest request);

    /**
     * Update an existing product.
     */
    Product update(Long id, ProductUpdateRequest request);

    /**
     * Get product by ID.
     */
    Product getById(Long id);

    /**
     * List all products.
     */
    List<Product> list();

    /**
     * Delete a product by ID.
     */
    void delete(Long id);
}
