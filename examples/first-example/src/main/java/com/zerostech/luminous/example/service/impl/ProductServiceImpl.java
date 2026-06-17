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

package com.zerostech.luminous.example.service.impl;

import com.zerostech.luminous.common.exception.LuminousBizException;
import com.zerostech.luminous.example.dto.ProductCreateRequest;
import com.zerostech.luminous.example.dto.ProductUpdateRequest;
import com.zerostech.luminous.example.model.Product;
import com.zerostech.luminous.example.service.ProductService;
import com.zerostech.luminous.web.Lu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Product service implementation using in-memory store.
 * <p>
 * Demonstrates:
 * <ul>
 *   <li>{@code LuminousBizException} for business errors</li>
 *   <li>{@code Lu.basic.cache} for caching (when Redis is available)</li>
 *   <li>{@code Lu.basic.lock} for distributed locking (when Redis is available)</li>
 * </ul>
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ConcurrentHashMap<Long, Product> store = new ConcurrentHashMap<>();
    private volatile long idSequence = 1L;

    @Override
    public Product create(ProductCreateRequest request) {
        // Demonstrate distributed locking via Lu facade
        if (Lu.isPresentLock()) {
            try {
                Lu.basic.lock.instance("product:create").tryLockWithFun(3000, 5000, () -> {
                    log.info("Acquired lock for product creation");
                });
            } catch (Exception e) {
                LuminousBizException.processLockFail("Failed to acquire lock for product creation");
            }
        }

        // Demonstrate duplicate check using business exception
        boolean duplicate = store.values().stream()
                .anyMatch(p -> p.getName().equals(request.getName()));
        if (duplicate) {
            LuminousBizException.processCheckDuplicate("Product with name '{}' already exists", request.getName());
        }

        Product product = Product.builder()
                .id(idSequence++)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .createTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .build();

        store.put(product.getId(), product);

        // Demonstrate caching via Lu facade
        if (Lu.isPresentCache()) {
            Lu.basic.cache.setex("product:" + product.getId(), product.getName(), 300);
            log.info("Cached product:{} for 300 seconds", product.getId());
        }

        return product;
    }

    @Override
    public Product update(Long id, ProductUpdateRequest request) {
        Product product = store.get(id);
        if (product == null) {
            LuminousBizException.processCheckDataNotExist("Product with id {} not found", String.valueOf(id));
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        product.setModifyTime(LocalDateTime.now());

        store.put(id, product);

        // Update cache
        if (Lu.isPresentCache()) {
            Lu.basic.cache.setex("product:" + id, product.getName(), 300);
        }

        return product;
    }

    @Override
    public Product getById(Long id) {
        // Demonstrate cache read
        if (Lu.isPresentCache()) {
            String cached = Lu.basic.cache.get("product:" + id);
            if (cached != null) {
                log.info("Cache hit for product:{}, cached name: {}", id, cached);
            }
        }

        Product product = store.get(id);
        if (product == null) {
            LuminousBizException.processCheckDataNotExist("Product with id {} not found", String.valueOf(id));
        }
        return product;
    }

    @Override
    public List<Product> list() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(Long id) {
        Product removed = store.remove(id);
        if (removed == null) {
            LuminousBizException.processCheckDataNotExist("Product with id {} not found", String.valueOf(id));
        }
        // Remove from cache
        if (Lu.isPresentCache()) {
            Lu.basic.cache.del("product:" + id);
        }
    }
}
