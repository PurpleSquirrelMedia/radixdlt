/*
 * Wallet/Explorer Api
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 2.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * ValidatorProperties
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-10-25T08:11:19.875906-07:00[America/Los_Angeles]")
public class ValidatorProperties {
  public static final String SERIALIZED_NAME_VALIDATOR_ADDRESS = "validatorAddress";
  @SerializedName(SERIALIZED_NAME_VALIDATOR_ADDRESS)
  private String validatorAddress;

  public static final String SERIALIZED_NAME_URL = "url";
  @SerializedName(SERIALIZED_NAME_URL)
  private String url;

  public static final String SERIALIZED_NAME_VALIDATOR_FEE = "validatorFee";
  @SerializedName(SERIALIZED_NAME_VALIDATOR_FEE)
  private String validatorFee;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_REGISTERED = "registered";
  @SerializedName(SERIALIZED_NAME_REGISTERED)
  private Boolean registered;

  public static final String SERIALIZED_NAME_OWNER_ADDRESS = "ownerAddress";
  @SerializedName(SERIALIZED_NAME_OWNER_ADDRESS)
  private String ownerAddress;

  public static final String SERIALIZED_NAME_EXTERNAL_STAKE_ACCEPTED = "externalStakeAccepted";
  @SerializedName(SERIALIZED_NAME_EXTERNAL_STAKE_ACCEPTED)
  private Boolean externalStakeAccepted;


  public ValidatorProperties validatorAddress(String validatorAddress) {
    
    this.validatorAddress = validatorAddress;
    return this;
  }

   /**
   * Get validatorAddress
   * @return validatorAddress
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getValidatorAddress() {
    return validatorAddress;
  }


  public void setValidatorAddress(String validatorAddress) {
    this.validatorAddress = validatorAddress;
  }


  public ValidatorProperties url(String url) {
    
    this.url = url;
    return this;
  }

   /**
   * Get url
   * @return url
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getUrl() {
    return url;
  }


  public void setUrl(String url) {
    this.url = url;
  }


  public ValidatorProperties validatorFee(String validatorFee) {
    
    this.validatorFee = validatorFee;
    return this;
  }

   /**
   * Get validatorFee
   * @return validatorFee
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getValidatorFee() {
    return validatorFee;
  }


  public void setValidatorFee(String validatorFee) {
    this.validatorFee = validatorFee;
  }


  public ValidatorProperties name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public ValidatorProperties registered(Boolean registered) {
    
    this.registered = registered;
    return this;
  }

   /**
   * Get registered
   * @return registered
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public Boolean getRegistered() {
    return registered;
  }


  public void setRegistered(Boolean registered) {
    this.registered = registered;
  }


  public ValidatorProperties ownerAddress(String ownerAddress) {
    
    this.ownerAddress = ownerAddress;
    return this;
  }

   /**
   * Get ownerAddress
   * @return ownerAddress
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getOwnerAddress() {
    return ownerAddress;
  }


  public void setOwnerAddress(String ownerAddress) {
    this.ownerAddress = ownerAddress;
  }


  public ValidatorProperties externalStakeAccepted(Boolean externalStakeAccepted) {
    
    this.externalStakeAccepted = externalStakeAccepted;
    return this;
  }

   /**
   * Get externalStakeAccepted
   * @return externalStakeAccepted
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public Boolean getExternalStakeAccepted() {
    return externalStakeAccepted;
  }


  public void setExternalStakeAccepted(Boolean externalStakeAccepted) {
    this.externalStakeAccepted = externalStakeAccepted;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidatorProperties validatorProperties = (ValidatorProperties) o;
    return Objects.equals(this.validatorAddress, validatorProperties.validatorAddress) &&
        Objects.equals(this.url, validatorProperties.url) &&
        Objects.equals(this.validatorFee, validatorProperties.validatorFee) &&
        Objects.equals(this.name, validatorProperties.name) &&
        Objects.equals(this.registered, validatorProperties.registered) &&
        Objects.equals(this.ownerAddress, validatorProperties.ownerAddress) &&
        Objects.equals(this.externalStakeAccepted, validatorProperties.externalStakeAccepted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(validatorAddress, url, validatorFee, name, registered, ownerAddress, externalStakeAccepted);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidatorProperties {\n");
    sb.append("    validatorAddress: ").append(toIndentedString(validatorAddress)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    validatorFee: ").append(toIndentedString(validatorFee)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    registered: ").append(toIndentedString(registered)).append("\n");
    sb.append("    ownerAddress: ").append(toIndentedString(ownerAddress)).append("\n");
    sb.append("    externalStakeAccepted: ").append(toIndentedString(externalStakeAccepted)).append("\n");
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
