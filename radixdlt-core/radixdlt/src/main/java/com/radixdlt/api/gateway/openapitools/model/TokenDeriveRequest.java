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
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;


/**
 * TokenDeriveRequest
 */
@JsonPropertyOrder({
  TokenDeriveRequest.JSON_PROPERTY_NETWORK,
  TokenDeriveRequest.JSON_PROPERTY_CREATOR_ACCOUNT_IDENTIFIER,
  TokenDeriveRequest.JSON_PROPERTY_SYMBOL
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-12-01T17:41:28.920972-06:00[America/Chicago]")
public class TokenDeriveRequest {
  public static final String JSON_PROPERTY_NETWORK = "network";
  private String network;

  public static final String JSON_PROPERTY_CREATOR_ACCOUNT_IDENTIFIER = "creator_account_identifier";
  private AccountIdentifier creatorAccountIdentifier;

  public static final String JSON_PROPERTY_SYMBOL = "symbol";
  private String symbol;


  public TokenDeriveRequest network(String network) {
    this.network = network;
    return this;
  }

   /**
   * Get network
   * @return network
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_NETWORK)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getNetwork() {
    return network;
  }


  @JsonProperty(JSON_PROPERTY_NETWORK)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setNetwork(String network) {
    this.network = network;
  }


  public TokenDeriveRequest creatorAccountIdentifier(AccountIdentifier creatorAccountIdentifier) {
    this.creatorAccountIdentifier = creatorAccountIdentifier;
    return this;
  }

   /**
   * Get creatorAccountIdentifier
   * @return creatorAccountIdentifier
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_CREATOR_ACCOUNT_IDENTIFIER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public AccountIdentifier getCreatorAccountIdentifier() {
    return creatorAccountIdentifier;
  }


  @JsonProperty(JSON_PROPERTY_CREATOR_ACCOUNT_IDENTIFIER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCreatorAccountIdentifier(AccountIdentifier creatorAccountIdentifier) {
    this.creatorAccountIdentifier = creatorAccountIdentifier;
  }


  public TokenDeriveRequest symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

   /**
   * Get symbol
   * @return symbol
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_SYMBOL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getSymbol() {
    return symbol;
  }


  @JsonProperty(JSON_PROPERTY_SYMBOL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }


  /**
   * Return true if this TokenDeriveRequest object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TokenDeriveRequest tokenDeriveRequest = (TokenDeriveRequest) o;
    return Objects.equals(this.network, tokenDeriveRequest.network) &&
        Objects.equals(this.creatorAccountIdentifier, tokenDeriveRequest.creatorAccountIdentifier) &&
        Objects.equals(this.symbol, tokenDeriveRequest.symbol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(network, creatorAccountIdentifier, symbol);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TokenDeriveRequest {\n");
    sb.append("    network: ").append(toIndentedString(network)).append("\n");
    sb.append("    creatorAccountIdentifier: ").append(toIndentedString(creatorAccountIdentifier)).append("\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
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

}

