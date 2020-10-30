package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Builder(access = AccessLevel.PUBLIC)   // To allow access to base class fields from a `Builder` instance.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductGetRequestBody implements Serializable
{
	@JsonProperty @NonNull	private String clientProductId;
}
