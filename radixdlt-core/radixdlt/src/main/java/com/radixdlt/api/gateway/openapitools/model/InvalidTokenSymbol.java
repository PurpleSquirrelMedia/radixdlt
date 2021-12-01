/*
 * Radix Gateway API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.9.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.radixdlt.api.gateway.openapitools.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.radixdlt.api.gateway.openapitools.JSON;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * InvalidTokenSymbol
 */
@JsonPropertyOrder({
  InvalidTokenSymbol.JSON_PROPERTY_INVALID_TOKEN_SYMBOL
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-12-01T17:41:28.920972-06:00[America/Chicago]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = InvalidTokenRRI.class, name = "InvalidTokenRRI"),
  @JsonSubTypes.Type(value = TokenNotFound.class, name = "TokenNotFound"),
})

public class InvalidTokenSymbol extends ErrorDetails {
  public static final String JSON_PROPERTY_INVALID_TOKEN_SYMBOL = "invalid_token_symbol";
  private String invalidTokenSymbol;


  public InvalidTokenSymbol invalidTokenSymbol(String invalidTokenSymbol) {
    this.invalidTokenSymbol = invalidTokenSymbol;
    return this;
  }

   /**
   * Get invalidTokenSymbol
   * @return invalidTokenSymbol
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_INVALID_TOKEN_SYMBOL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getInvalidTokenSymbol() {
    return invalidTokenSymbol;
  }


  @JsonProperty(JSON_PROPERTY_INVALID_TOKEN_SYMBOL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setInvalidTokenSymbol(String invalidTokenSymbol) {
    this.invalidTokenSymbol = invalidTokenSymbol;
  }


  /**
   * Return true if this InvalidTokenSymbol object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvalidTokenSymbol invalidTokenSymbol = (InvalidTokenSymbol) o;
    return Objects.equals(this.invalidTokenSymbol, invalidTokenSymbol.invalidTokenSymbol) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(invalidTokenSymbol, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvalidTokenSymbol {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    invalidTokenSymbol: ").append(toIndentedString(invalidTokenSymbol)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

static {
  // Initialize and register the discriminator mappings.
  Map<String, Class<?>> mappings = new HashMap<String, Class<?>>();
  mappings.put("InvalidTokenRRI", InvalidTokenRRI.class);
  mappings.put("TokenNotFound", TokenNotFound.class);
  mappings.put("InvalidTokenSymbol", InvalidTokenSymbol.class);
  JSON.registerDiscriminator(InvalidTokenSymbol.class, "type", mappings);
}
}

