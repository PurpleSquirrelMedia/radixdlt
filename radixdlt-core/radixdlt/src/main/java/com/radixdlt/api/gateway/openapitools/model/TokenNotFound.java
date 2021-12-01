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
 * TokenNotFound
 */
@JsonPropertyOrder({
  TokenNotFound.JSON_PROPERTY_TOKEN_NOT_FOUND
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-12-01T17:07:44.365935-06:00[America/Chicago]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = InvalidTokenRRI.class, name = "InvalidTokenRRI"),
  @JsonSubTypes.Type(value = TokenNotFound.class, name = "TokenNotFound"),
})

public class TokenNotFound extends ErrorDetails {
  public static final String JSON_PROPERTY_TOKEN_NOT_FOUND = "token_not_found";
  private TokenIdentifier tokenNotFound;


  public TokenNotFound tokenNotFound(TokenIdentifier tokenNotFound) {
    this.tokenNotFound = tokenNotFound;
    return this;
  }

   /**
   * Get tokenNotFound
   * @return tokenNotFound
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_TOKEN_NOT_FOUND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public TokenIdentifier getTokenNotFound() {
    return tokenNotFound;
  }


  @JsonProperty(JSON_PROPERTY_TOKEN_NOT_FOUND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setTokenNotFound(TokenIdentifier tokenNotFound) {
    this.tokenNotFound = tokenNotFound;
  }


  /**
   * Return true if this TokenNotFound object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TokenNotFound tokenNotFound = (TokenNotFound) o;
    return Objects.equals(this.tokenNotFound, tokenNotFound.tokenNotFound) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tokenNotFound, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TokenNotFound {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    tokenNotFound: ").append(toIndentedString(tokenNotFound)).append("\n");
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
  mappings.put("TokenNotFound", TokenNotFound.class);
  JSON.registerDiscriminator(TokenNotFound.class, "type", mappings);
}
}

