package com.company.rest.products.model.sample_jsons.post;

import com.company.rest.products.util.json_objects.ProductPostRequestBody;

/**
 * An agglomeration of pre-build POST requests to test our API with.
 */
public class GoodPostRequests
{


	public static final ProductPostRequestBody[] POST_REQUESTS =
	{

			/* ********************************************************************** */
			/* **************************** FLOWERS ********************************* */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1/8 oz") // Gets internally capitalized
				                        .productType("flower")  //  Gets internally capitalized
				                        .costInCents(2500L)
									.build(),

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1/4 oz")
				                        .productType("flower")
				                        .costInCents(5000L)
										.labelColor("106B0C") // Dark greenish
									.build(),

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1/2 oz")
				                        .productType("flower")
				                        .costInCents(10000L)
									.build(),

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1 oz")
				                        .productType("flower")
				                        .costInCents(16000L)
										.labelColor("B9650C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 2 oz")
			                            .productType("flower")
			                            .costInCents(280000L)
			                            .labelColor("F96A0C")
										.availableForPickup(true)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/8 oz")
			                            .productType("flower")
			                            .costInCents(5500L)
			                            .labelColor("E5FA90")
										.availableForPickup(true)
										.availableElectronically(false)
										.availableOnline(false)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/4 oz")
			                            .productType("flower")
			                            .costInCents(8000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/2 oz")
			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("10670C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Pernicious Insolence 1/8 oz")
			                            .productType("flower")
			                            .costInCents(3000L)
			                            .labelColor("102BAC")
										.availableForPickup(true)
										.sku("TH01132301")
										.upc("042100005264")
										.description("How did you ever live without this?")
										.categoryId("HIGH-THC")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Pernicious Insolence 1/4 oz")
			                            .productType("flower")
			                            .costInCents(5500L)
										.sku("AB01132301")
										.upc("7921022123729")
										.description("How did you ever live without this?")
										.categoryId("HIGH-THC")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Everest Kush 1/8 oz")
			                            .productType("flower")
			                            .costInCents(4000L)
			                            .labelColor("BB5890")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Everest Kush 1/4 oz")
			                            .productType("flower")
			                            .costInCents(7500L)
			                            .labelColor("BBAF78")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Everest Kush 1/2 oz")
			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("CC3467")
										.sku("JJ908721")
										.upc("1009564232")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Bobby Flay 1/4 oz")
			                            .productType("flower")
			                            .costInCents(6500L)
			                            .labelColor("106DDD")
										.categoryId("HIGH-CBD")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Bobby Flay 1/2 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("FB8905")
										.categoryId("HIGH-CBD")
										.availableForPickup(true)
										.availableOnline(true)
										.availableElectronically(true)
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Bedouin Daydream 1/8 oz")
			                            .productType("flower")
			                            .costInCents(5000L)
			                            .labelColor("AA62B2")
										.categoryId("POPCORN")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Bedouin Daydream 1/4 oz")
			                            .productType("flower")
			                            .costInCents(9000L)
			                            .labelColor("5962BB")
										.categoryId("POPCORN")
										.availableElectronically(true)
										.availableForPickup(false)
										.availableForPickup(true) // This should be allowable, and just re-writes the value
										.availableOnline(false)
										.description("Now on sale!")
									.build(),


			ProductPostRequestBody.builder()
			                            .name("Mindy Kohen's Attorney 1/8 oz")
			                            .productType("flower")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
										.sku("BB67BB21")
										.description("It's not Cochran")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Mindy Kohen's Attorney 1/4 oz")
			                            .productType("flower")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
			                            .description("It's still not Cochran")
										.sku("BB67BB21")
									.build(),


			/* ********************************************************************** */
			/* **************************** VAPORIZERS ******************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Midnight Delight 0.5g")
			                            .productType("flower")
			                            .costInCents(5500L)
			                            .labelColor("AA89BD")
										.categoryId("HIGH-CBD")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Midnight Delight 1g")
			                            .productType("flower")
			                            .costInCents(7500L)
			                            .labelColor("BB89BA")
										.categoryId("HIGH-CBD")
										.description("Now on sale! Perfect for sleep-related issues!")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/4 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/4 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/4 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/4 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1 oz")
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build()




			/* ********************************************************************** */
			/* **************************** TOPICALS ******************************** */
			/* ********************************************************************** */




			/* ********************************************************************** */
			/* **************************** EDIBLES ******************************** */
			/* ********************************************************************** */




			/* ********************************************************************** */
			/* **************************** PETS ******************************** */
			/* ********************************************************************** */


	};
}
