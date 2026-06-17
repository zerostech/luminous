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

package com.zerostech.luminous.example.controller;

import com.zerostech.luminous.common.response.Result;
import com.zerostech.luminous.example.dto.ProductCreateRequest;
import com.zerostech.luminous.example.dto.ProductUpdateRequest;
import com.zerostech.luminous.example.model.Product;
import com.zerostech.luminous.example.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product controller — demonstrates full CRUD with:
 * <ul>
 *   <li>{@code Result<T>} unified API response</li>
 *   <li>{@code @Valid} Jakarta Validation on request DTOs</li>
 *   <li>{@code LuminousBizException} handled by global ExceptionControllerAdvice</li>
 * </ul>
 */
@Tag(name = "Product", description = "Product CRUD operations")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a product", description = "Validates input with Jakarta Validation and returns the created product")
    @PostMapping
    public Result<Product> create(@Valid @RequestBody ProductCreateRequest request) {
        return Result.success(productService.create(request));
    }

    @Operation(summary = "Update a product", description = "Partially updates a product; returns 404 if not found")
    @PutMapping("/{id}")
    public Result<Product> update(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return Result.success(productService.update(id, request));
    }

    @Operation(summary = "Get product by ID", description = "Returns 404 (B0003) if product not found")
    @GetMapping("/{id}")
    public Result<Product> getById(@Parameter(description = "Product ID") @PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "List all products")
    @GetMapping
    public Result<List<Product>> list() {
        return Result.success(productService.list());
    }

    @Operation(summary = "Delete a product", description = "Returns 404 (B0003) if product not found")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "Product ID") @PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }
}
