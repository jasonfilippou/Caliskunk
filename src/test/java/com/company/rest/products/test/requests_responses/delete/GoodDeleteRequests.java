package com.company.rest.products.test.requests_responses.delete;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
/**
 * Some DELETE requests that match the items inserted in {@link GoodPostRequests}.
 *
 * @see com.company.rest.products.test.requests_responses.get.GoodGetRequests
 * @see GoodPostRequests
 */
public class GoodDeleteRequests
{
	/**
	 * An array of prepared {@link ProductDeleteRequestBody} instances. They match the instances in {@link GoodPostRequests}.
	 */
	public static final ProductDeleteRequestBody[] REQUESTS =
			{
					/* ********************************************************************** */
					/* **************************** FLOWERS ********************************* */
					/* ********************************************************************** */
					
					new ProductDeleteRequestBody("#CULNEC-EIGHTH"),
					new ProductDeleteRequestBody("#CULNEC-QUARTER"),
					new ProductDeleteRequestBody("#CULNEC-HALF"),
					new ProductDeleteRequestBody("#CULNEC-ONE"),
					new ProductDeleteRequestBody("#CULNEC-TWO"),
					new ProductDeleteRequestBody("#SILGOR-EIGHTH"),
					new ProductDeleteRequestBody("#SILGOR-QUARTER"),
					new ProductDeleteRequestBody("#SILGOR-HALF"),
					new ProductDeleteRequestBody("#PERN-INC-EIGHTH"),
					new ProductDeleteRequestBody("#PERN-INC-QUARTER"),
					new ProductDeleteRequestBody("#EVKUSH-EIGHTH"),
					new ProductDeleteRequestBody("#EVKUSH-QUARTER"),
					new ProductDeleteRequestBody("#EVKUSH-HALF"),
					new ProductDeleteRequestBody("#BOBFLAY-QUARTER"),
					new ProductDeleteRequestBody("#BOBFLAY-HALF"),
					new ProductDeleteRequestBody("#BOBFLAY-EIGHTH"),
					new ProductDeleteRequestBody("#BEDOUIN-QUARTER"),
					new ProductDeleteRequestBody("#MKA-EIGHTH"),
					new ProductDeleteRequestBody("#MKA-QUARTER"),

					/* ********************************************************************** */
					/* **************************** VAPORIZERS ******************************** */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#MIDDEL-HALFGRAM"),
					new ProductDeleteRequestBody("#MIDDEL-ONEGRAM"),
					new ProductDeleteRequestBody("#DOG-HALFGRAM"),
					new ProductDeleteRequestBody("#DOG-ONEGRAM"),
					new ProductDeleteRequestBody("#AFTBOOST-DISP"),

					/* ********************************************************************** */
					/* **************************** TOPICALS ******************************** */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#SYNKUSH"),
					new ProductDeleteRequestBody("#TRANQUIL"),
					new ProductDeleteRequestBody("#JULY"),

					/* ********************************************************************** */
					/* **************************** EDIBLES ******************************** */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#BETTY-50"),
					new ProductDeleteRequestBody("#BETTY-25"),
					new ProductDeleteRequestBody("#PAR-10"),
					new ProductDeleteRequestBody("#WEEDPASTA"),

					/* ********************************************************************** */
					/* **************************** TINCTURES ******************************* */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#MARY-200"),
					new ProductDeleteRequestBody("#DREAM-100"),
					new ProductDeleteRequestBody("#DREAM-200"),
					new ProductDeleteRequestBody("#C-CHOICE-100"),
					new ProductDeleteRequestBody("#NL-100"),

					/* ********************************************************************** */
					/* **************************** CONCENTRATES ******************************* */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#GHOST-2G"),
					new ProductDeleteRequestBody("#BLACK-DEATH-1G"),
					new ProductDeleteRequestBody("#KEMAL-1G"),
					new ProductDeleteRequestBody("#WHITE-PERSIAN-1G"),

					/* ********************************************************************** */
					/* **************************** PETS ************************************ */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#TAILWAGGER-50"),
					new ProductDeleteRequestBody("#TAILWAGGER-100"),
					new ProductDeleteRequestBody("#FELINE"),

					/* ********************************************************************** */
					/* **************************** PREROLLS ***************************** */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#PINEXP-PREROLL"),
					new ProductDeleteRequestBody("#CLANDGIR"),
					new ProductDeleteRequestBody("#CULNEC-PREROLL"),

					/* ********************************************************************** */
					/* **************************** ACCESSORIES ***************************** */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#MEESEEKS-BONG"),
					new ProductDeleteRequestBody("#RESPTOK-TRAY"),

					/* ********************************************************************** */
					/* **************************** OTHER ****************************** */
					/* ********************************************************************** */

					new ProductDeleteRequestBody("#PTS-500"),
					new ProductDeleteRequestBody("#PTS-1000"),
					new ProductDeleteRequestBody("#SEEDS-TWOOZ")
			};

	/**
	 * A more readable alias for {@link #REQUESTS}.
	 */
	public static final ProductDeleteRequestBody[] GOOD_DELETES = REQUESTS;
}
