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
import java.util.ArrayList;
import java.util.List;
import org.openapitools.client.model.ConstructionBuildErrorResponse;
import org.openapitools.client.model.ConstructionBuildResponse;
import org.openapitools.client.model.ConstructionBuildSuccessResponse;
import org.openapitools.client.model.ConstructionBuildSuccessResponseAllOf;

/**
 * ConstructionBuildSuccessResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-10-25T08:11:19.875906-07:00[America/Los_Angeles]")
public class ConstructionBuildSuccessResponse extends ConstructionBuildResponse {
  public static final String SERIALIZED_NAME_FEE = "fee";
  @SerializedName(SERIALIZED_NAME_FEE)
  private String fee;

  public static final String SERIALIZED_NAME_UNSIGNED_TRANSACTION = "unsignedTransaction";
  @SerializedName(SERIALIZED_NAME_UNSIGNED_TRANSACTION)
  private String unsignedTransaction;

  public static final String SERIALIZED_NAME_PAYLOAD_TO_SIGN = "payloadToSign";
  @SerializedName(SERIALIZED_NAME_PAYLOAD_TO_SIGN)
  private String payloadToSign;

  public static final String SERIALIZED_NAME_NOTIFICATIONS = "notifications";
  @SerializedName(SERIALIZED_NAME_NOTIFICATIONS)
  private List<Object> notifications = new ArrayList<Object>();

  public ConstructionBuildSuccessResponse() {
    this.result = this.getClass().getSimpleName();
  }

  public ConstructionBuildSuccessResponse fee(String fee) {
    
    this.fee = fee;
    return this;
  }

   /**
   * Get fee
   * @return fee
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getFee() {
    return fee;
  }


  public void setFee(String fee) {
    this.fee = fee;
  }


  public ConstructionBuildSuccessResponse unsignedTransaction(String unsignedTransaction) {
    
    this.unsignedTransaction = unsignedTransaction;
    return this;
  }

   /**
   * Get unsignedTransaction
   * @return unsignedTransaction
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getUnsignedTransaction() {
    return unsignedTransaction;
  }


  public void setUnsignedTransaction(String unsignedTransaction) {
    this.unsignedTransaction = unsignedTransaction;
  }


  public ConstructionBuildSuccessResponse payloadToSign(String payloadToSign) {
    
    this.payloadToSign = payloadToSign;
    return this;
  }

   /**
   * Get payloadToSign
   * @return payloadToSign
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public String getPayloadToSign() {
    return payloadToSign;
  }


  public void setPayloadToSign(String payloadToSign) {
    this.payloadToSign = payloadToSign;
  }


  public ConstructionBuildSuccessResponse notifications(List<Object> notifications) {
    
    this.notifications = notifications;
    return this;
  }

  public ConstructionBuildSuccessResponse addNotificationsItem(Object notificationsItem) {
    this.notifications.add(notificationsItem);
    return this;
  }

   /**
   * Get notifications
   * @return notifications
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public List<Object> getNotifications() {
    return notifications;
  }


  public void setNotifications(List<Object> notifications) {
    this.notifications = notifications;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConstructionBuildSuccessResponse constructionBuildSuccessResponse = (ConstructionBuildSuccessResponse) o;
    return Objects.equals(this.fee, constructionBuildSuccessResponse.fee) &&
        Objects.equals(this.unsignedTransaction, constructionBuildSuccessResponse.unsignedTransaction) &&
        Objects.equals(this.payloadToSign, constructionBuildSuccessResponse.payloadToSign) &&
        Objects.equals(this.notifications, constructionBuildSuccessResponse.notifications) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fee, unsignedTransaction, payloadToSign, notifications, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConstructionBuildSuccessResponse {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    fee: ").append(toIndentedString(fee)).append("\n");
    sb.append("    unsignedTransaction: ").append(toIndentedString(unsignedTransaction)).append("\n");
    sb.append("    payloadToSign: ").append(toIndentedString(payloadToSign)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
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
