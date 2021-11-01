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
 * TransactionInfoRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-10-25T08:11:19.875906-07:00[America/Los_Angeles]")
public class TransactionInfoRequest {
  public static final String SERIALIZED_NAME_TRANSACTION_IDENTIFIER = "transactionIdentifier";
  @SerializedName(SERIALIZED_NAME_TRANSACTION_IDENTIFIER)
  private String transactionIdentifier;


  public TransactionInfoRequest transactionIdentifier(String transactionIdentifier) {
    
    this.transactionIdentifier = transactionIdentifier;
    return this;
  }

   /**
   * Get transactionIdentifier
   * @return transactionIdentifier
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getTransactionIdentifier() {
    return transactionIdentifier;
  }


  public void setTransactionIdentifier(String transactionIdentifier) {
    this.transactionIdentifier = transactionIdentifier;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionInfoRequest transactionInfoRequest = (TransactionInfoRequest) o;
    return Objects.equals(this.transactionIdentifier, transactionInfoRequest.transactionIdentifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionIdentifier);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionInfoRequest {\n");
    sb.append("    transactionIdentifier: ").append(toIndentedString(transactionIdentifier)).append("\n");
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
