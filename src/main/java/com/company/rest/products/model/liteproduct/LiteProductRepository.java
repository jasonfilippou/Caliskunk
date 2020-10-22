package com.company.rest.products.model.liteproduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface LiteProductRepository extends JpaRepository<LiteProduct, Long>
{
	Optional<LiteProduct> findByName(String name);

	Optional<LiteProduct> findBySquareItemId(String id);
}