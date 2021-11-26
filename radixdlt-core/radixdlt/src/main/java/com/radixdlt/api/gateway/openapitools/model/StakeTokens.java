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
 * StakeTokens
 */
@JsonPropertyOrder({
  StakeTokens.JSON_PROPERTY_FROM,
  StakeTokens.JSON_PROPERTY_TO,
  StakeTokens.JSON_PROPERTY_AMOUNT
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-11-25T22:01:18.117974-06:00[America/Chicago]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BurnTokens.class, name = "BurnTokens"),
  @JsonSubTypes.Type(value = MintTokens.class, name = "MintTokens"),
  @JsonSubTypes.Type(value = StakeTokens.class, name = "StakeTokens"),
  @JsonSubTypes.Type(value = TransferTokens.class, name = "TransferTokens"),
  @JsonSubTypes.Type(value = UnstakeTokens.class, name = "UnstakeTokens"),
})

public class StakeTokens extends Action {
  public static final String JSON_PROPERTY_FROM = "from";
  private AccountIdentifier from;

  public static final String JSON_PROPERTY_TO = "to";
  private ValidatorIdentifier to;

  public static final String JSON_PROPERTY_AMOUNT = "amount";
  private TokenAmount amount;


  public StakeTokens from(AccountIdentifier from) {
    this.from = from;
    return this;
  }

   /**
   * Get from
   * @return from
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_FROM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public AccountIdentifier getFrom() {
    return from;
  }


  @JsonProperty(JSON_PROPERTY_FROM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFrom(AccountIdentifier from) {
    this.from = from;
  }


  public StakeTokens to(ValidatorIdentifier to) {
    this.to = to;
    return this;
  }

   /**
   * Get to
   * @return to
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_TO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ValidatorIdentifier getTo() {
    return to;
  }


  @JsonProperty(JSON_PROPERTY_TO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTo(ValidatorIdentifier to) {
    this.to = to;
  }


  public StakeTokens amount(TokenAmount amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Get amount
   * @return amount
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_AMOUNT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public TokenAmount getAmount() {
    return amount;
  }


  @JsonProperty(JSON_PROPERTY_AMOUNT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAmount(TokenAmount amount) {
    this.amount = amount;
  }


  /**
   * Return true if this StakeTokens object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StakeTokens stakeTokens = (StakeTokens) o;
    return Objects.equals(this.from, stakeTokens.from) &&
        Objects.equals(this.to, stakeTokens.to) &&
        Objects.equals(this.amount, stakeTokens.amount) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, amount, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StakeTokens {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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
  mappings.put("BurnTokens", BurnTokens.class);
  mappings.put("MintTokens", MintTokens.class);
  mappings.put("StakeTokens", StakeTokens.class);
  mappings.put("TransferTokens", TransferTokens.class);
  mappings.put("UnstakeTokens", UnstakeTokens.class);
  mappings.put("StakeTokens", StakeTokens.class);
  JSON.registerDiscriminator(StakeTokens.class, "type", mappings);
}
}
