package com.company.rest.products.model.liteproduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * A MySQL-friendly {@link JpaRepository}.
 * @see LiteProduct
 */
@Component
public interface LiteProductRepository extends JpaRepository<LiteProduct, Long>
{
	Optional<LiteProduct> findByProductName(String name);

	Optional<LiteProduct> findBySquareItemId(String id);

	Optional<LiteProduct> findByClientProductId(String id);

	void deleteByClientProductId(String id);
}
