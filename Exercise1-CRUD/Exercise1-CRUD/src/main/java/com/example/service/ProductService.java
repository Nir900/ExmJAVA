package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Product;
import com.example.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public List<Product> createProducts(List<Product> products) {
        return repository.saveAll(products);
    }
    
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!")
        );
    }

    public Product updateProduct(Long id, Product details) {
        Product existing = getProductById(id);
        existing.setName(details.getName());
        existing.setPrice(details.getPrice());
        existing.setQuantity(details.getQuantity());
        existing.setDescription(details.getDescription());
        return repository.save(existing);
    } 

    public void deleteProduct(Long id) {
        repository.delete(getProductById(id));
    }
}
