package com.company.rest.products.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiteProductRepository extends JpaRepository<LiteProduct, Long>
{
	Optional<LiteProduct> findById(Long queryId);
}
