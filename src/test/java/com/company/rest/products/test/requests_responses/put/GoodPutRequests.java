package com.company.rest.products.test.requests_responses.put;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

import static com.company.rest.products.test.util.TestUtil.DEFAULT_VERSION;
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

			/* ********************************************************************** */
			/* ******************* FORMER "FLOWERS" IN POST REQUEST ***************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
										.productName("Culeothesis Necrosis 1/8 oz")
			                            .version(DEFAULT_VERSION)
				                        .productType("vaporizer") // Change from "flower"
				                        .costInCents(2500L)
										
									.build(),

			ProductUpsertRequestBody.builder()
										.productName("Culeothesis Necrosis 1/4 oz")
                                        .version(DEFAULT_VERSION)
				                        .productType("flower")
				                        .costInCents(6000L)         // Change in cost
										.labelColor("122B0C")       // Change in label color
                                        
									.build(),

			ProductUpsertRequestBody.builder()                       // No changes here; should still be possible to PUT
										.productName("Culeothesis Necrosis 1/2 oz")
                                        .version(DEFAULT_VERSION)
			                                 // Field not provided in relevant POST
			                            .labelColor("BB6677")               // Same as above.
				                        .productType("flower")
				                        .costInCents(15000L)                // And a change in cost for good measure.
				                        
									.build(),

			ProductUpsertRequestBody.builder()
										.productName("Culeothesis Necrosis 1 oz")
                                        .version(DEFAULT_VERSION)
				                        .productType("flower")
				                        .costInCents(16000L)
										.labelColor("B9650C")
                                        
										.description("The best Cul Nec deal yet!")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Culeothesis Necrosis 2 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("vaporizer")
			                            .costInCents(280000L)
			                            .labelColor("F96A0D")
			                            
									.build(),

			ProductUpsertRequestBody.builder()              // Removed fields from relevant POST
			                        .productName("Senile Gorilla 1/8 oz")      // And changed name
			                        .version(DEFAULT_VERSION)
			                        .productType("topical")
			                        .costInCents(5500L)
			                        .labelColor("E5FA90")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Senile Gorilla 1/4 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("topical")
			                            .costInCents(8000L)
			                            .labelColor("106B0C")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Senile Gorilla 1/2 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("20673C")
										.sku("SKU_WHICH_WAS_NOT_IN_POST")
										.upc("UPC_WHICH_WAS_NOT_IN_POST")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Pernicious Insolence 1/8 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(3000L)
			                            .labelColor("102BAC")

										.sku("TH01132301")
										.upc("042100005264")
										.description("How did you ever live without this?")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Pernicious Insolence 1/4 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(4500L)
										.sku("AB01132301")
										.upc("7921022123729")
										.description("How did you ever live without this?")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                        .productName("Jurgen Klopp 1/8 oz")        // What
			                        .version(DEFAULT_VERSION)
			                        .productType("flower")
			                        .costInCents(4000L)
			                        .labelColor("BB5890")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Jurgen Klopp 1/4 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(7000L)
			                            .labelColor("BBAF78")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Jurgen Klopp 1/2 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(12000L)
			                            .labelColor("CC3467")
										.sku("JJ908721")
										.upc("1009564232")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Bobby Flay's Nightmare 1/4 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(6500L)
			                            .labelColor("106DDD")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Bobby Flay's Nightmare 1/2 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("FB8905")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Bedouin Daydream 1/8 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("tincture")
			                            .costInCents(5000L)
			                            .labelColor("AA62B2")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Bedouin Daydream 1/4 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(9000L)
			                            .labelColor("5962BB")
										.description("Now on sale!")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Mindy Kohen's Attorney 1/8 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("vaporizer")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
										.sku("BB67BB21")
										.description("It's not Cochran")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Mindy Kohen's Attorney 1/4 oz")
			                            .version(DEFAULT_VERSION)
			                            .productType("vaporizer")
			                            .costInCents(2500L)
			                            .labelColor("67FA25")
			                            .description("It's still not Cochran")
										.sku("BB67BB21")
                                        
									.build(),


			/* ********************************************************************** */
			/* ************************* FORMER "VAPORIZERS" ************************ */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()  // Change all of the POSTed fields, but add / remove *no field*.
			                            .productName("Canine Delight 0.5g")
			                            .version(DEFAULT_VERSION)
			                            .productType("pet")
			                            .costInCents(2500L)
			                            .labelColor("ABA9BD")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Canine Delight 1g")
			                            .version(DEFAULT_VERSION)
			                            .productType("pet")
			                            .costInCents(4000L)
			                            .labelColor("BB89BF")
										.description("Now on sale! Perfect for sleep-related issues!")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Deathstar OG 0.5g")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(6000L)
			                            .labelColor("106B0C")
										.description("It will knock you out.")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Deathstar OG 1g")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .labelColor("89BB1D")
										.description("It will knock you out.")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Afternoon Booster 0.3g disposable cart")
			                            .version(DEFAULT_VERSION)
			                            .productType("vaporizer")
			                            .costInCents(1500L)
			                            .labelColor("89BB1D")
										.description("Disposable cartridge. On sale!")
										.sku("ADDED_SKU")       // But no UPC
                                        
									.build(),

			/* ********************************************************************** */
			/* **************************** FORMER "TOPICALS" *********************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("Synergy Kush 120mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(4000L)
			                            .labelColor("51F90A")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Tranquil Northern Wind 100mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("vaporizer")
			                            .costInCents(45000L)
			                            .labelColor("10AD14")
										.description("Our newest product!")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("July 200mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("topical")
			                            .costInCents(16000L)
			                            .labelColor("106B0C")
			                        
										.upc("RANDOM_UPC")          // But no SKU
									.build(),

			/* ********************************************************************** */
			/* ************************** FORMER "EDIBLES" ************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("Betty's Eddies 10 x 50mg gummies")
			                            .version(DEFAULT_VERSION)
			                            .productType("edible")
			                            .costInCents(4500L)
			                            .labelColor("AAF55F")
			                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Betty's Eddies 10 x 25mg gummies")
			                            .version(DEFAULT_VERSION)
			                            .productType("edible")
			                            .costInCents(3000L)
			                            .labelColor("AAF55F")   // No reason to not have same label color
			                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Paradise scent 10x10mg chews")
			                            .version(DEFAULT_VERSION)
			                            .productType("edible")
			                            .costInCents(2500L)
			                            .labelColor("106B0C")
										.upc("042100AAA264")
                                        .description("Flash sale! While supplies last!")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Weed Pasta")
			                            .version(DEFAULT_VERSION)
			                            .productType("edible")
			                            .costInCents(14000L)
			                            .description("Enough said.")
			                            
									.build(),

			/* ********************************************************************** */
			/* ********************* FORMER "TINCTURES" ***************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("Mary's Medicinal's 200mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("edible")
			                            .costInCents(10000L)
			                            .labelColor("106B0C")
				                        
									.build(),

			ProductUpsertRequestBody.builder()
			                        .productName("Dreamy Tincture 100mg")
			                        .version(DEFAULT_VERSION)
			                        .productType("tincture")
			                        .costInCents(5500L)
			                        .productName("Cloudy Mixture 100mg")       // Allowable
			                        .productName("Cloudy Mixture 120mg")       // And again
			                        .labelColor("60B555")
			                        .description("Excellent for daydreamers!")
                                        
					.build(),

			ProductUpsertRequestBody.builder()          // Just the basics for this PUT
			                            .productName("Dreamy Tincture 200mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("tincture")
			                            .costInCents(8000L)
			                        
					.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Cleopatra's Choice 100mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("topical")
			                            .description(null)      // Should be allowed, and treated equivalently to it not being there
			                            .costInCents(80000L)
			                        
										.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Northern Lights 100mg")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(10000L)
			                            .costInCents(111000L)   // Allowed
										.description("The famous strain, here to make you travel to lands unknown.")
                                        
									.build(),

			/* ********************************************************************** */
			/* ********************** FORMER "CONCENTRATES" ************************* */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("Ghost Shatter 2g")
			                            .version(DEFAULT_VERSION)
			                            .productType("concentrate")
			                            .costInCents(20000L)
			                            .labelColor("FAF578")
										.description("Prepare to leave planet Earth.")
										.sku(null)      // Should be allowed
										.upc(null)      // Should also be allowed
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("The Black Death 1g")
			                            .version(DEFAULT_VERSION)
			                            .productType("concentrate")
			                            .costInCents(12000L)
			                            .labelColor("106A0C")
										.description("Holy crap.")
										.upc("042100005264")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Kemal Ataturk 1g")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(10000L)
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("White Persian 1.5g")
			                            .version(DEFAULT_VERSION)
			                            .productType("edible")
			                            .costInCents(15000L)
			                            .labelColor("FF895F")
			                            
									.build(),


			/* ********************************************************************** */
			/* *************************** FORMER "PETS" **************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("Doggie Treats 50ml")
			                            .version(DEFAULT_VERSION)
			                            .productType("pet")
			                            .costInCents(3000L)
									.build(),

			ProductUpsertRequestBody.builder()
			                        .productName("DoGgIe TReATs 100ML")        // name is case-insensitive
			                        .version(DEFAULT_VERSION)
			                        .productType("Topical")             // type is also case-insensitive
			                        .productType("pet")
			                        .costInCents(5000L)
			                        .description("Now on sale!")
			                        .description("Now on sale!")        // Should be allowed
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Feline Tocker 40ml")
			                            .version(DEFAULT_VERSION)
			                            .productType("pet")
			                            .costInCents(2500L)
			                            .description("For kittens 6 months or older.")
			                            
									.build(),

			/* ********************************************************************** */
			/* ********************* FORMER "PREROLLS" ****************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("Tally Mon 1g")
			                            .version(DEFAULT_VERSION)
			                            .productType("preroll")
			                            .costInCents(1200L)
			                            .labelColor("22AB0D")
										.description("Will help you take the edge of")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Clandestine Sloth 1/8")
			                            .version(DEFAULT_VERSION)
			                            .productType("flower")
			                            .costInCents(1500L)
			                            .labelColor("A52C6C")
			                            
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Culeothesis Necrosis 1.5g infused")
			                            .version(DEFAULT_VERSION)
			                            .productType("preroll")
			                            .costInCents(2500L)
			                            .labelColor("10221D")
										.description("Paper dipped in concentrates before rolling.")
                                        
									.build(),


			/* ********************************************************************** */
			/* ******************** FORMER "ACCESSORIES" **************************** */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("\"Mr Meeseeks\" limited edition bong")
			                            .version(DEFAULT_VERSION)
			                            .productType("accessory")
			                            .costInCents(32000L)
			                            .labelColor("C459A1")
										.description("Ah geez, Rick!")
                                        
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("\"Responsible Toker\" rolling tray")
			                            .version(DEFAULT_VERSION)
			                            .productType("accessory")
			                            .costInCents(1500L)
										.description("Gotta keep things clean.")
                                        
									.build(),

			/* ********************************************************************** */
			/* ******************* FORMER "OTHER" ************************************* */
			/* ********************************************************************** */

			ProductUpsertRequestBody.builder()
			                            .productName("500 points bundle")
			                            .version(DEFAULT_VERSION)
			                            .productType("other")
			                            .costInCents(4000L)
			                            .labelColor("106B0C")
										.description("Not combine-able with other offers...")
									.build(),

			ProductUpsertRequestBody.builder() // Full gamut of fields except for ID
			                            .productName("1,000 points bundle")
			                            .version(DEFAULT_VERSION)
			                            .productType("other")
			                            .costInCents(6000L)
			                            .labelColor("106B0C")
										.description("Biggest value for money yet!")
										.upc("MY_UPC")
										.sku("MY_SKU")
									.build(),

			ProductUpsertRequestBody.builder()
			                            .productName("Sativa Seeds, 2oz")
			                            .version(DEFAULT_VERSION)
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
