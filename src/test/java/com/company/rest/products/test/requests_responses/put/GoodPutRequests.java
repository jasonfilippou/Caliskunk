package com.company.rest.products.test.requests_responses.put;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

/**
 * Some sample POST requests for end-to-end testing.
 *
 * @see com.company.rest.products.test.requests_responses.get.GoodGetRequests
 * @see com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests
 * @see GoodPostRequests
 * @see BadPutRequests
 */
public class GoodPutRequests
{

	/**
	 * An array of prepared {@link ProductUpsertRequestBody} instances. Every one of the elements will have at least
	 * one change when compared to the corresponding element of {@link GoodPostRequests#REQUESTS}, to ensure that the updates
	 * happen accordingly.
	 *
	 * @see GoodPostRequests#REQUESTS
	 */
	public static final ProductUpsertRequestBody[] REQUESTS =
	{

			/* The string "FORMER" in the comments below is meant to bring home the fact mentioned in the
			 * JavaDoc comment above: every one of these elements correspond to an element of the GOOD_POSTS
			 * array, where the product type is specified. But since a PUT request might also change the product type,
			 * we have some products that will change their type, hence the word "FORMER" to really point to the
			 * fact that it _could_ be the case that the product changes its type in this PUT request.
			 *
			 * Another important fact here is that not one of these PUT requests contain a product ID! This is because
			 * whenever we send a PUT to our API, the ID is included in the URI. While possible, it should not be
			 * assumed that the client has included the ID inside their request body. So we ensure we are robust to this.
			 */

			/* ********************************************************************** */
			/* ******************* FORMER "FLOWERS" IN POST REQUEST ***************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
										.name("Culeothesis Necrosis 1/8 oz")

				                        .productType("vaporizer") // Change from "flower"
				                        .costInCents(2500L)
									.build(),

			ProductUpsertRequestBody.builder()
										.name("Culeothesis Necrosis 1/4 oz")

				                        .productType("flower")
				                        .costInCents(6000L)         // Change in cost
										.labelColor("122B0C")       // Change in label color
									.build(),

			ProductUpsertRequestBody.builder()                       // No changes here; should still be possible to PUT
										.name("Culeothesis Necrosis 1/2 oz")
			                            .availableElectronically(false)     // Field not provided in relevant POST
			                            .labelColor("BB6677")               // Same as above.
				                        .productType("flower")
				                        .costInCents(15000L)                // And a change in cost for good measure.
									.build(),

			ProductUpsertRequestBody.builder()
										.name("Culeothesis Necrosis 1 oz")

				                        .productType("flower")
				                        .costInCents(16000L)
										.labelColor("B9650C")
										.availableForPickup(true)
										.description("The best Cul Nec deal yet!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Culeothesis Necrosis 2 oz")

			                            .productType("vaporizer")
			                            .costInCents(280000L)
			                            .labelColor("F96A0D")
										.availableForPickup(true)
									.build(),

			ProductUpsertRequestBody.builder()              // Removed fields from relevant POST
			                            .name("Senile Gorilla 1/8 oz")      // And changed name

			                            .productType("topical")
			                            .costInCents(5500L)
			                            .labelColor("E5FA90")
										.availableOnline(false)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Senile Gorilla 1/4 oz")

			                            .productType("topical")
			                            .costInCents(8000L)
			                            .labelColor("106B0C")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Senile Gorilla 1/2 oz")

			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("20673C")
										.sku("SKU_WHICH_WAS_NOT_IN_POST")
										.upc("UPC_WHICH_WAS_NOT_IN_POST")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Pernicious Insolence 1/8 oz")

			                            .productType("flower")
			                            .costInCents(3000L)
			                            .labelColor("102BAC")
										.availableForPickup(true)
										.sku("TH01132301")
										.upc("042100005264")
										.description("How did you ever live without this?")

									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Pernicious Insolence 1/4 oz")

			                            .productType("flower")
			                            .costInCents(4500L)
										.sku("AB01132301")
										.upc("7921022123729")
										.description("How did you ever live without this?")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Jurgen Klopp 1/8 oz")        // What

			                            .productType("flower")
			                            .costInCents(4000L)
			                            .labelColor("BB5890")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Jurgen Klopp 1/4 oz")

			                            .productType("flower")
			                            .costInCents(7000L)
			                            .labelColor("BBAF78")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Jurgen Klopp 1/2 oz")

			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("CC3467")
										.sku("JJ908721")
										.upc("1009564232")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Bobby Flay's Nightmare 1/4 oz")

			                            .productType("flower")
			                            .costInCents(6500L)
			                            .labelColor("106DDD")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Bobby Flay's Nightmare 1/2 oz")

			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("FB8905")

										.availableForPickup(true)
										.availableOnline(true)
										.availableElectronically(true)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Bedouin Daydream 1/8 oz")

			                            .productType("tincture")
			                            .costInCents(5000L)
			                            .labelColor("AA62B2")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Bedouin Daydream 1/4 oz")

			                            .productType("flower")
			                            .costInCents(9000L)
			                            .labelColor("5962BB")
										.availableElectronically(true)
										.availableForPickup(false)
										.availableForPickup(true) // This should be allowable, and just re-writes the value
										.availableOnline(false)
										.description("Now on sale!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Mindy Kohen's Attorney 1/8 oz")

			                            .productType("vaporizer")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
										.sku("BB67BB21")
										.description("It's not Cochran")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Mindy Kohen's Attorney 1/4 oz")

			                            .productType("vaporizer")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
			                            .description("It's still not Cochran")
										.sku("BB67BB21")
										.availableForPickup(true)
										.availableElectronically(true)
										.availableOnline(true)
									.build(),


			/* ********************************************************************** */
			/* ************************* FORMER "VAPORIZERS" ************************ */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()  // Change all of the POSTed fields, but add / remove *no field*.
			                            .name("Canine Delight 0.5g")

			                            .productType("pet")
			                            .costInCents(2500L)
			                            .labelColor("ABA9BD")

									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Canine Delight 1g")

			                            .productType("pet")
			                            .costInCents(4000L)
			                            .labelColor("BB89BF")

										.description("Now on sale! Perfect for sleep-related issues!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Deathstar OG 0.5g")

			                            .productType("flower")
			                            .costInCents(6000L)
			                            .labelColor("106B0C")
										.description("It will knock you out.")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Deathstar OG 1g")

			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("89BB1D")
										.description("It will knock you out.")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Afternoon Booster 0.3g disposable cart")

			                            .productType("vaporizer")
			                            .costInCents(1500L)
			                            .labelColor("89BB1D")
										.description("Disposable cartridge. On sale!")
										.availableOnline(true)
										.availableForPickup(true)
										.sku("ADDED_SKU")       // But no UPC
									.build(),

			/* ********************************************************************** */
			/* **************************** FORMER "TOPICALS" *********************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("Synergy Kush 120mg")

			                            .productType("flower")
			                            .costInCents(4000L)
			                            .labelColor("51F90A")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Tranquil Northern Wind 100mg")

			                            .productType("vaporizer")
			                            .costInCents(45000L)
			                            .labelColor("10AD14")
										.description("Our newest product!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("July 200mg")

			                            .productType("topical")
			                            .costInCents(16000L)
			                            .labelColor("106B0C")
										.availableForPickup(false)
										.availableForPickup(true) // Possible, given builder pattern
										.upc("RANDOM_UPC")          // But no SKU
									.build(),

			/* ********************************************************************** */
			/* ************************** FORMER "EDIBLES" ************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("Betty's Eddies 10 x 50mg gummies")

			                            .productType("edible")
			                            .costInCents(4500L)
			                            .labelColor("AAF55F")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Betty's Eddies 10 x 25mg gummies")

			                            .productType("edible")
			                            .costInCents(3000L)
			                            .labelColor("AAF55F")   // No reason to not have same label color
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Paradise scent 10x10mg chews")

			                            .productType("edible")
			                            .costInCents(2500L)
			                            .labelColor("106B0C")
										.upc("042100AAA264")
										.availableForPickup(true)
										.availableOnline(false)
										.availableElectronically(false)
										.description("Flash sale! While supplies last!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Weed Pasta")

			                            .productType("edible")
			                            .costInCents(14000L)
			                            .description("Enough said.")
										.availableForPickup(true)
									.build(),

			/* ********************************************************************** */
			/* ********************* FORMER "TINCTURES" ***************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("Mary's Medicinal's 200mg")

			                            .productType("edible")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")

									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Dreamy Tincture 100mg")

			                            .productType("tincture")
			                            .costInCents(5500L)
			                            .name("Cloudy Mixture 100mg")       // Allowable
			                            .name("Cloudy Mixture 120mg")       // And again
			                            .labelColor("60B555")
										.description("Excellent for daydreamers!")
									.build(),

			ProductUpsertRequestBody.builder()          // Just the basics for this PUT
			                            .name("Dreamy Tincture 200mg")

			                            .productType("tincture")
			                            .costInCents(8000L)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Cleopatra's Choice 100mg")

			                            .productType("topical")
			                            .description(null)      // Should be allowed, and treated equivalently to it not being there
			                            .costInCents(80000L)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Northern Lights 100mg")

			                            .productType("flower")
			                            .costInCents(10000L)
			                            .costInCents(111000L)   // Allowed
										.description("The famous strain, here to make you travel to lands unknown.")
									.build(),

			/* ********************************************************************** */
			/* ********************** FORMER "CONCENTRATES" ************************* */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("Ghost Shatter 2g")

			                            .productType("concentrate")
			                            .costInCents(20000L)
			                            .labelColor("FAF578")

										.description("Prepare to leave planet Earth.")
										.sku(null)      // Should be allowed
										.upc(null)      // Should also be allowed
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("The Black Death 1g")

			                            .productType("concentrate")
			                            .costInCents(12000L)
			                            .labelColor("106A0C")
										.description("Holy crap.")
										.upc("042100005264")
									.build(),
			ProductUpsertRequestBody.builder()
			                            .name("Kemal Ataturk 1g")

			                            .productType("flower")
			                            .costInCents(10000L)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("White Persian 1.5g")

			                            .productType("edible")
			                            .costInCents(15000L)
			                            .labelColor("FF895F")

									.build(),


			/* ********************************************************************** */
			/* *************************** FORMER "PETS" **************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("Doggie Treats 50ml")

			                            .productType("pet")
			                            .costInCents(3000L)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("DoGgIe TReATs 100ML")        // name is case-insensitive

			                            .productType("Topical")             // type is also case-insensitive
			                            .productType("pet")
			                            .costInCents(5000L)
										.description("Now on sale!")
										.description("Now on sale!")        // Should be allowed
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Feline Tocker 40ml")

			                            .productType("pet")
			                            .costInCents(2500L)
			                            .description("For kittens 6 months or older.")
										.availableForPickup(true)
										.availableElectronically(true)
										.availableOnline(true)

									.build(),

			/* ********************************************************************** */
			/* ********************* FORMER "PREROLLS" ****************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("Tally Mon 1g")

			                            .productType("preroll")
			                            .costInCents(1200L)
			                            .labelColor("22AB0D")
										.description("Will help you take the edge of")

									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Clandestine Sloth 1/8")

			                            .productType("flower")
			                            .costInCents(1500L)
			                            .labelColor("A52C6C")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Culeothesis Necrosis 1.5g infused")
			                            .productType("preroll")

			                            .costInCents(2500L)
			                            .labelColor("10221D")
										.description("Paper dipped in concentrates before rolling.")
									.build(),


			/* ********************************************************************** */
			/* ******************** FORMER "ACCESSORIES" **************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("\"Mr Meeseeks\" limited edition bong")

			                            .productType("accessory")
			                            .costInCents(32000L)
			                            .labelColor("C459A1")
										.description("Ah geez, Rick!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("\"Responsible Toker\" rolling tray")

			                            .productType("accessory")
			                            .costInCents(1500L)
										.description("Gotta keep things clean.")
									.build(),

			/* ********************************************************************** */
			/* ******************* FORMER "OTHER" ************************************* */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .name("500 points bundle")

			                            .productType("other")
			                            .costInCents(4000L)
			                            .labelColor("106B0C")
										.description("Not combine-able with other offers...")
									.build(),

			ProductUpsertRequestBody.builder() // Full gamut of fields except for ID
			                            .name("1,000 points bundle")

			                            .productType("other")
			                            .costInCents(6000L)
			                            .labelColor("106B0C")
										.description("Biggest value for money yet!")
										.upc("MY_UPC")
										.sku("MY_SKU")
										.availableOnline(true)

										.availableElectronically(false)
										.availableForPickup(true)
									.build(),

			ProductUpsertRequestBody.builder()
			                            .name("Sativa Seeds, 2oz")

			                            .productType("accessory")
			                            .costInCents(300000L)
			                            .labelColor("126B0C")
										.description("1/2 Pineapple Express and 1/2 Skywalker OG")
										.sku("C761BB2211")
			                            .upc("042100005264")
										.upc("DDECA0FBBA3F")        // Overwrite should be allowed
									.build()
	};

	/**
	 * A more readable alias for {@link #REQUESTS}.
	 */
	public static final ProductUpsertRequestBody[] GOOD_PUTS = REQUESTS;
}
