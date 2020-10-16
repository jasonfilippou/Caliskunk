package com.company.rest.products;

import com.company.rest.products.model.LiteProduct;
import com.company.rest.products.model.LiteProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j  // For `log` reference to a `EmployeeControllerLogger` instance that is configured for this class.
public class LoadDatabase
{
	private void preload(LiteProductRepository repository, String name, String type, Long costInCents)
	{
		log.info("Preloading" + repository.save(LiteProduct
													.builder()
														.name(name)
														.type(type)
														.costInCents(costInCents)
													.build()
													));
	}
	@Bean
// So that the Spring Container runs it. More here: http://zetcode.com/springboot/commandlinerunner/
	CommandLineRunner initDatabase(LiteProductRepository repository)
	{
		return args ->
		{
			// Adding an 'L' at the end of the cost arg to ensure it is parsed as Long.
			preload(repository, "Bertrand Montgomery", "flower", 6000L);
			preload(repository, "Kuleothesis Necrosis", "flower", 5500L);
			preload(repository, "Midnight Delight", "topical", 3000L);
			preload(repository, "Giraffe Kush", "flower", 7000L);
			preload(repository, "Deathstar OG", "vaporizer", 7500L);
			preload(repository, "Mindy Kohen's Attorney", "vaporizer", 7500L);
			preload(repository, "Coconut Heaven", "topical", 3000L);
			preload(repository, "Betty's Eddies", "edible", 2500L);
			preload(repository, "Curio Wellness", "edible", 2500L);
			preload(repository, "Salamander the Great", "flower", 8000L);
		};
	}
}
