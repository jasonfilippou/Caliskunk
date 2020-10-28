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
			                            .clientProductId("CULNEC-EIGHTH")
				                        .productType("flower")  //  Gets internally capitalized
				                        .costInCents(2500L)
									.build(),

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1/4 oz")
			                            .clientProductId("CULNEC-QUARTER")
				                        .productType("flower")
				                        .costInCents(5000L)
										.labelColor("106B0C") // Dark greenish
									.build(),

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1/2 oz")
			                            .clientProductId("CULNEC-HALF")
				                        .productType("flower")
				                        .costInCents(10000L)
									.build(),

			ProductPostRequestBody.builder()
										.name("Culeothesis Necrosis 1 oz")
			                            .clientProductId("CULNEC-ONE")
				                        .productType("flower")
				                        .costInCents(16000L)
										.labelColor("B9650C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 2 oz")
			                            .clientProductId("CULNEC-TWO")
			                            .productType("flower")
			                            .costInCents(280000L)
			                            .labelColor("F96A0C")
										.availableForPickup(true)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/8 oz")
			                            .clientProductId("SILGOR-EIGHTH")
			                            .productType("flower")
			                            .costInCents(5500L)
			                            .labelColor("E5FA90")
										.availableForPickup(true)
										.availableElectronically(false)
										.availableOnline(false)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/4 oz")
			                            .clientProductId("SILGOR-QUARTER")
			                            .productType("flower")
			                            .costInCents(8000L)
			                            .labelColor("106B0C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Silverback Gorilla 1/2 oz")
			                            .clientProductId("SILGOR-HALF")
			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("10670C")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Pernicious Insolence 1/8 oz")
			                            .clientProductId("PERN-INC-EIGHTH")
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
			                            .clientProductId("PERN-INC-QUARTER")
			                            .productType("flower")
			                            .costInCents(5500L)
										.sku("AB01132301")
										.upc("7921022123729")
										.description("How did you ever live without this?")
										.categoryId("HIGH-THC")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Everest Kush 1/8 oz")
			                            .clientProductId("EVKUSH-EIGHTH")
			                            .productType("flower")
			                            .costInCents(4000L)
			                            .labelColor("BB5890")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Everest Kush 1/4 oz")
			                            .clientProductId("EVKUSH-QUARTER")
			                            .productType("flower")
			                            .costInCents(7500L)
			                            .labelColor("BBAF78")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Everest Kush 1/2 oz")
			                            .clientProductId("EVKUSH-HALF")
			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("CC3467")
										.sku("JJ908721")
										.upc("1009564232")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Bobby Flay 1/4 oz")
			                            .clientProductId("BOBFLAY-QUARTER")
			                            .productType("flower")
			                            .costInCents(6500L)
			                            .labelColor("106DDD")
										.categoryId("HIGH-CBD")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Bobby Flay 1/2 oz")
			                            .clientProductId("BOBFLAY-HALF")
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
			                            .clientProductId("BOBFLAY-EIGHTH")
			                            .productType("flower")
			                            .costInCents(5000L)
			                            .labelColor("AA62B2")
										.categoryId("POPCORN")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Bedouin Daydream 1/4 oz")
			                            .clientProductId("BOBFLAY-QUARTER")
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
			                            .clientProductId("MKA-EIGHTH")
			                            .productType("flower")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
										.sku("BB67BB21")
										.description("It's not Cochran")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Mindy Kohen's Attorney 1/4 oz")
			                            .clientProductId("MKA-QUARTER")
			                            .productType("flower")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
			                            .description("It's still not Cochran")
										.sku("BB67BB21")
										.sku("BB6BBB21")        // Allowed
									.build(),


			/* ********************************************************************** */
			/* **************************** VAPORIZERS ******************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Midnight Delight 0.5g")
			                            .clientProductId("MIDDEL-HALFGRAM")
			                            .productType("vaporizer")
			                            .costInCents(5500L)
			                            .labelColor("AA89BD")
										.categoryId("HIGH-CBD")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Midnight Delight 1g")
			                            .clientProductId("MIDDEL-ONEGRAM")
			                            .productType("vaporizer")
			                            .costInCents(7500L)
			                            .labelColor("BB89BA")
										.categoryId("HIGH-CBD")
										.description("Now on sale! Perfect for sleep-related issues!")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Deathstar OG 0.5g")
			                            .clientProductId("DOG-HALFGRAM")
			                            .productType("vaporizer")
			                            .costInCents(6000L)
			                            .labelColor("106B0C")
										.description("It will knock you out.")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Deathstar OG 1g")
			                            .clientProductId("DOG-ONEGRAM")
			                            .productType("vaporizer")
			                            .costInCents(10000L)
			                            .labelColor("89BB1D")
										.description("It will knock you out.")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Afternoon Booster 0.3g disposable cart")
			                            .clientProductId("AFTBOOST-DISP")
			                            .productType("vaporizer")
			                            .costInCents(2000L)
			                            .labelColor("89BB1D")
										.description("Disposable cartridge.")
										.availableOnline(true)
									.build(),

			/* ********************************************************************** */
			/* **************************** TOPICALS ******************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Synergy Kush 120mg")
			                            .clientProductId("SYNKUSH")
			                            .productType("topical")
			                            .costInCents(4000L)
			                            .labelColor("51F90A")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Tranquil Northern Wind 100mg")
			                            .clientProductId("TRANQUIL")
			                            .productType("topical")
			                            .costInCents(4000L)
			                            .labelColor("10AD14")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("July 200mg")
			                            .clientProductId("JULY")
			                            .productType("topical")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
									.build(),

			/* ********************************************************************** */
			/* **************************** EDIBLES ******************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Betty's Eddies 5 x 50mg gummies")
			                            .clientProductId("BETTY-50")
			                            .productType("edible")
			                            .costInCents(45000L)
			                            .labelColor("AAF55F")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Betty's Eddies 5 x 25mg gummies")
			                            .clientProductId("BETTY-25")
			                            .productType("edible")
			                            .costInCents(25000L)
			                            .labelColor("AAF55F")   // No reason to not have same label color
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Paradise scent 10x10mg chews")
			                            .clientProductId("PAR-10")
			                            .productType("edible")
			                            .costInCents(5000L)
			                            .labelColor("106B0C")
										.upc("042100AAA264")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Weed Pasta")
			                            .clientProductId("WEEDPASTA")
			                            .productType("edible")
			                            .costInCents(15000L)
			                            .description("Enough said.")
									.build(),

			/* ********************************************************************** */
			/* **************************** TINCTURES ******************************* */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Mary's Medicinal's 200mg")
			                            .clientProductId("MARY-200")
			                            .productType("tincture")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
										.categoryId("HIGH-CBD")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Dreamy Tincture 100mg")
			                            .clientProductId("DREAM-100")
			                            .productType("tincture")
			                            .costInCents(5000L)
			                            .name("Cloudy Mixture 100mg")       // Allowable
			                            .name("Cloudy Mixture 120mg")       // And again
			                            .labelColor("60B555")
										.description("Excellent for daydreamers!")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Dreamy Tincture 200mg")
			                            .clientProductId("DREAM-200")
			                            .productType("tincture")
			                            .costInCents(8000L)
										.sku("AAAF7B118")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Cleopatra's Choice 100mg")
			                            .clientProductId("C-CHOICE-100")
			                            .productType("tincture")
			                            .description(null)      // Should be allowed, and treated equivalently to it not being there
			                            .costInCents(80000L)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Northern Lights 100mg")
			                            .clientProductId("NL-100")
			                            .productType("tincture")
			                            .costInCents(10000L)
			                            .costInCents(111000L)   // Allowed
										.description("The famous strain, now in tincture form!")
									.build(),

			/* ********************************************************************** */
			/* **************************** CONCENTRATES ******************************* */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Ghost Shatter 2g")
			                            .clientProductId("GHOST-2G")
			                            .productType("concentrate")
			                            .costInCents(20000L)
			                            .labelColor("FAF678")
			                            .categoryId("HIGH-THC")
										.description("Prepare to leave planet Earth.")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("The Black Death 1g")
			                            .clientProductId("BLACK-DEATH-1G")
			                            .productType("concentrate")
			                            .costInCents(12000L)
			                            .labelColor("106B0C")
										.categoryId("HIGH-THC")
										.upc("042100005264")
									.build(),
			ProductPostRequestBody.builder()
			                            .name("Kemal Ataturk 1g")
			                            .clientProductId("KEMAL-1G")
			                            .productType("concentrate")
			                            .costInCents(10000L)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("White Persian 1.5g")
			                            .clientProductId("WHITE-PERSIAN-1G")
			                            .productType("concentrate")
			                            .costInCents(15000L)
			                            .labelColor("FF895F")
										.categoryId("HIGH-CBD")
									.build(),


			/* ********************************************************************** */
			/* **************************** PETS ************************************ */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Tailwagger 50ml")
			                            .clientProductId("TAILWAGGER-50")
			                            .productType("pet")
			                            .costInCents(3000L)
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Tailwagger 100ml")
			                            .clientProductId("TAILWAGGER-100")
			                            .productType("pet")
			                            .costInCents(5000L)
										.description("Now on sale!")
										.description("Now on sale!")        // Should be allowed
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Feline Tocker 40ml")
			                            .clientProductId("FELINE")
			                            .productType("pet")
			                            .costInCents(2700L)
			                            .description("For kittens 6 months or older.")
									.build(),

			/* ********************************************************************** */
			/* **************************** PREROLLS ***************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("Pineapple Express 1g")
       			                        .clientProductId("PINEXP-PREROLL")
			                            .productType("preroll")
			                            .costInCents(1200L)
			                            .labelColor("22AB0D")
										.description("Will help you take the edge of")
										.categoryId("HIGH-THC")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Clandestine Giraffe 1.5g")
			                            .clientProductId("CLANDGIR")
			                            .productType("preroll")
			                            .costInCents(1500L)
			                            .labelColor("A52C6A")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Culeothesis Necrosis 1.5g infused")
			                            .productType("preroll")
			                            .clientProductId("CULNEC-PREROLL")
			                            .costInCents(2500L)
			                            .labelColor("10221C")
										.description("Paper dipped in concentrates before rolling.")
										.categoryId("INFUSED")
									.build(),


			/* ********************************************************************** */
			/* **************************** ACCESSORIES ***************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("\"Mr Meeseeks\" limited edition bong")
			                            .clientProductId("MEESEEKS-BONG")
			                            .productType("accessory")
			                            .costInCents(25000L)
			                            .labelColor("C45981")
										.description("Ah geez, Rick!")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("\"Responsible Toker\" rolling tray")
			                            .clientProductId("RESPTOK-TRAY")
			                            .productType("accessory")
			                            .costInCents(1500L)
									.build(),

			/* ********************************************************************** */
			/* **************************** OTHER ****************************** */
			/* ********************************************************************** */

			ProductPostRequestBody.builder()
			                            .name("500 points bundle")
			                            .clientProductId("PTS-500")
			                            .productType("other")
			                            .costInCents(4000L)
			                            .labelColor("106B0C")
										.categoryId("POINTS")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("1,000 points bundle")
			                            .clientProductId("PTS-1000")
			                            .productType("other")
			                            .costInCents(70000L)
			                            .labelColor("106B0C")
										.description("Biggest value for money yet!")
										.categoryId("POINTS")
									.build(),

			ProductPostRequestBody.builder()
			                            .name("Sativa Seeds, 2oz")
			                            .clientProductId("SEEDS-TWOOZ")
			                            .productType("other")
			                            .costInCents(300000L)
			                            .labelColor("106B0C")
										.description("1/2 Pineapple Express and 1/2 Skywalker OG")
										.categoryId("SEEDS")
										.sku("C761BB2211")
			                            .upc("042100005264")
										.upc("DDECA0FBBA3F")        // Overwrite should be allowed
									.build()
	};
}
