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
 * NotValidatorOwnerError
 */
@JsonPropertyOrder({
  NotValidatorOwnerError.JSON_PROPERTY_OWNER,
  NotValidatorOwnerError.JSON_PROPERTY_USER
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-11-27T11:34:49.994520-06:00[America/Chicago]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BelowMinimumStakeError.class, name = "BelowMinimumStakeError"),
  @JsonSubTypes.Type(value = CouldNotConstructFeesError.class, name = "CouldNotConstructFeesError"),
  @JsonSubTypes.Type(value = MessageTooLongError.class, name = "MessageTooLongError"),
  @JsonSubTypes.Type(value = NotEnoughResourcesError.class, name = "NotEnoughResourcesError"),
  @JsonSubTypes.Type(value = NotValidatorOwnerError.class, name = "NotValidatorOwnerError"),
})

public class NotValidatorOwnerError extends TransactionBuildError {
  public static final String JSON_PROPERTY_OWNER = "owner";
  private AccountIdentifier owner;

  public static final String JSON_PROPERTY_USER = "user";
  private AccountIdentifier user;


  public NotValidatorOwnerError owner(AccountIdentifier owner) {
    this.owner = owner;
    return this;
  }

   /**
   * Get owner
   * @return owner
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_OWNER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public AccountIdentifier getOwner() {
    return owner;
  }


  @JsonProperty(JSON_PROPERTY_OWNER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setOwner(AccountIdentifier owner) {
    this.owner = owner;
  }


  public NotValidatorOwnerError user(AccountIdentifier user) {
    this.user = user;
    return this;
  }

   /**
   * Get user
   * @return user
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_USER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public AccountIdentifier getUser() {
    return user;
  }


  @JsonProperty(JSON_PROPERTY_USER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUser(AccountIdentifier user) {
    this.user = user;
  }


  /**
   * Return true if this NotValidatorOwnerError object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotValidatorOwnerError notValidatorOwnerError = (NotValidatorOwnerError) o;
    return Objects.equals(this.owner, notValidatorOwnerError.owner) &&
        Objects.equals(this.user, notValidatorOwnerError.user) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, user, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotValidatorOwnerError {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
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
  mappings.put("BelowMinimumStakeError", BelowMinimumStakeError.class);
  mappings.put("CouldNotConstructFeesError", CouldNotConstructFeesError.class);
  mappings.put("MessageTooLongError", MessageTooLongError.class);
  mappings.put("NotEnoughResourcesError", NotEnoughResourcesError.class);
  mappings.put("NotValidatorOwnerError", NotValidatorOwnerError.class);
  mappings.put("NotValidatorOwnerError", NotValidatorOwnerError.class);
  JSON.registerDiscriminator(NotValidatorOwnerError.class, "type", mappings);
}
}
