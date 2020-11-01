package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Builder(access = AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDeleteRequestBody implements Serializable
{
	@JsonProperty @NonNull	private String clientProductId;
}
