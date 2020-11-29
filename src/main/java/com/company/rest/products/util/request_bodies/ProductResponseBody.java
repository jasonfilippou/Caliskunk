package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.BackendService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * Information about the object that the user made an API call for. Is contained within an instance of
 * {@link org.springframework.http.ResponseEntity} when {@link com.company.rest.products.controller.ProductController}
 * returns it.
 *
 * @see ProductGetRequestBody
 * @see ProductUpsertRequestBody
 * @see ProductDeleteRequestBody
 * @see ProductUpsertRequestBody
 */
@Data
@Builder(access = AccessLevel.PUBLIC)
public class ProductResponseBody implements Serializable
{
	// For now this class is almost identical to BackendServiceResponseBody, in order for us
	// to give a breadth of information to the user. However, this can change,
	// so separate logic can be useful when refactoring.
	@JsonProperty("id") @NonNull private String clientProductId;    // Provided by client.
	@JsonProperty("name")  @NonNull	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost_in_cents")  @NonNull private Long costInCents;
	@JsonProperty("description")  private String description;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
	@JsonProperty("version") private Long version;          // Needed for PUT queries.
	@JsonProperty("is_deleted") private Boolean isDeleted;
	@JsonProperty("updated_at") private String updatedAt;

	/**
	 * Build a response data element based on information from {@link BackendService}.
	 * @param backendResponse An instance of {@link BackendServiceResponseBody} that provides us with information about
	 *                        the run of {@link BackendService}.
	 *
	 * @return An instance of {@link ProductResponseBody} which will be used by {@link com.company.rest.products.controller.ProductController}
	 * to provide the client with an appropriate {@link org.springframework.http.ResponseEntity}.
	 */
	public static ProductResponseBody fromBackendResponseBody(final BackendServiceResponseBody backendResponse)
	{
		return builder()
				.clientProductId(backendResponse.getClientProductId())
				.name(backendResponse.getName())
				.productType(backendResponse.getProductType())
				.costInCents(backendResponse.getCostInCents())
				.description(backendResponse.getDescription())
				.isDeleted(backendResponse.getIsDeleted())
				.labelColor(backendResponse.getLabelColor())
				.upc(backendResponse.getUpc())
				.sku(backendResponse.getSku())
				.version(backendResponse.getVersion())
				.updatedAt(backendResponse.getUpdatedAt())
				.build();
	}
}
