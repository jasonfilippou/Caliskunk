package com.company.rest.products;

import com.company.rest.products.model.LiteProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j  // For `log` reference to a `EmployeeControllerLogger` instance that is configured for this class.
public class LoadDatabase
{
	@Bean
// So that the Spring Container runs it. More here: http://zetcode.com/springboot/commandlinerunner/
	CommandLineRunner initDatabase(LiteProductRepository repository)
	{
		return args ->
		{
//			log.info("Preloading" + repository.save
//					(new LiteProduct("Blueberry Kush", "flower")));
//			log.info("Preloading" + repository.save
//					(new LiteProduct("Bertrand Montgomery", "flower")));
//			log.info("Preloading" + repository.save
//					(new LiteProduct("Midnight", "topical")));
//			log.info("Preloading" + repository.save
//					(new LiteProduct("Pineapple Express", "vaporizer")));
		};
	}
}
