package com.company.rest.products.test.requests_responses.get;

import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;

/**
 * Some GET requests that match the items inserted in {@link GoodPostRequests}.
 *
 * @see GoodPostRequests
 * @see com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests
 * @see BadGetRequests
 */
public class GoodGetRequests
{

	/** An array of prepared {@link ProductGetRequestBody} instances. They match the instances in {@link GoodPostRequests}.
	 */
	public static final ProductGetRequestBody[] REQUESTS =
			{
					/* ********************************************************************** */
					/* **************************** FLOWERS ********************************* */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#CULNEC-EIGHTH"),
					new ProductGetRequestBody("#CULNEC-QUARTER"),
					new ProductGetRequestBody("#CULNEC-HALF"),
					new ProductGetRequestBody("#CULNEC-ONE"),
					new ProductGetRequestBody("#CULNEC-TWO"),
					new ProductGetRequestBody("#SILGOR-EIGHTH"),
					new ProductGetRequestBody("#SILGOR-QUARTER"),
					new ProductGetRequestBody("#SILGOR-HALF"),
					new ProductGetRequestBody("#PERN-INC-EIGHTH"),
					new ProductGetRequestBody("#PERN-INC-QUARTER"),
					new ProductGetRequestBody("#EVKUSH-EIGHTH"),
					new ProductGetRequestBody("#EVKUSH-QUARTER"),
					new ProductGetRequestBody("#EVKUSH-HALF"),
					new ProductGetRequestBody("#BOBFLAY-QUARTER"),
					new ProductGetRequestBody("#BOBFLAY-HALF"),
					new ProductGetRequestBody("#BOBFLAY-EIGHTH"),
					new ProductGetRequestBody("#BEDOUIN-QUARTER"),
					new ProductGetRequestBody("#MKA-EIGHTH"),
					new ProductGetRequestBody("#MKA-QUARTER"),                    
					
					/* ********************************************************************** */
					/* **************************** VAPORIZERS ******************************** */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#MIDDEL-HALFGRAM"),
					new ProductGetRequestBody("#MIDDEL-ONEGRAM"),
					new ProductGetRequestBody("#DOG-HALFGRAM"),
					new ProductGetRequestBody("#DOG-ONEGRAM"),
					new ProductGetRequestBody("#AFTBOOST-DISP"),
					
					/* ********************************************************************** */
					/* **************************** TOPICALS ******************************** */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#SYNKUSH"),
					new ProductGetRequestBody("#TRANQUIL"),
					new ProductGetRequestBody("#JULY"),
					
					/* ********************************************************************** */
					/* **************************** EDIBLES ******************************** */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#BETTY-50"),
					new ProductGetRequestBody("#BETTY-25"),
					new ProductGetRequestBody("#PAR-10"),
					new ProductGetRequestBody("#WEEDPASTA"),
					
					/* ********************************************************************** */
					/* **************************** TINCTURES ******************************* */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#MARY-200"),
					new ProductGetRequestBody("#DREAM-100"),
					new ProductGetRequestBody("#DREAM-200"),
					new ProductGetRequestBody("#C-CHOICE-100"),
					new ProductGetRequestBody("#NL-100"),
					
					/* ********************************************************************** */
					/* **************************** CONCENTRATES ******************************* */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#GHOST-2G"),
					new ProductGetRequestBody("#BLACK-DEATH-1G"),
					new ProductGetRequestBody("#KEMAL-1G"),
					new ProductGetRequestBody("#WHITE-PERSIAN-1G"),                    
					
					/* ********************************************************************** */
					/* **************************** PETS ************************************ */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#TAILWAGGER-50"),
					new ProductGetRequestBody("#TAILWAGGER-100"),
					new ProductGetRequestBody("#FELINE"),
					
					/* ********************************************************************** */
					/* **************************** PREROLLS ***************************** */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#PINEXP-PREROLL"),
					new ProductGetRequestBody("#CLANDGIR"),
					new ProductGetRequestBody("#CULNEC-PREROLL"),                    
					
					/* ********************************************************************** */
					/* **************************** ACCESSORIES ***************************** */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#MEESEEKS-BONG"),
					new ProductGetRequestBody("#RESPTOK-TRAY"),
					
					/* ********************************************************************** */
					/* **************************** OTHER ****************************** */
					/* ********************************************************************** */
					
					new ProductGetRequestBody("#PTS-500"),
					new ProductGetRequestBody("#PTS-1000"),
					new ProductGetRequestBody("#SEEDS-TWOOZ")
			};
	/**
	 * A more readable alias for {@link #REQUESTS}.
	 */
	public static final ProductGetRequestBody[] GOOD_GETS = REQUESTS;
}
