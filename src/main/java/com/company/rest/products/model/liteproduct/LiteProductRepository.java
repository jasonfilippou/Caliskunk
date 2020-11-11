package com.company.rest.products.model.liteproduct;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * A MySQL-friendly {@link JpaRepository}.
 * @see LiteProduct
 */
@Component
public interface LiteProductRepository extends JpaRepository<LiteProduct, Long>
{
	Optional<LiteProduct> findByProductName(@NonNull String name);

	Optional<LiteProduct> findBySquareItemId(@NonNull String id);

	Optional<LiteProduct> findByClientProductId(@NonNull String id);

	@Transactional // Annotation added after advice found here: https://stackoverflow.com/questions/32269192/spring-no-entitymanager-with-actual-transaction-available-for-current-thread
	void deleteByClientProductId(@NonNull String id);
}
