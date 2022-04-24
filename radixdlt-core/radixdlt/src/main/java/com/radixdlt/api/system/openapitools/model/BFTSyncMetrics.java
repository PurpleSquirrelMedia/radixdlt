/*
 * Radix System API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.radixdlt.api.system.openapitools.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * BFTSyncMetrics
 */
@JsonPropertyOrder({
  BFTSyncMetrics.JSON_PROPERTY_REQUESTS_SENT,
  BFTSyncMetrics.JSON_PROPERTY_REQUESTS_RECEIVED,
  BFTSyncMetrics.JSON_PROPERTY_REQUEST_TIMEOUTS
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-01-20T22:11:26.095756951+01:00[Europe/Warsaw]")
public class BFTSyncMetrics {
  public static final String JSON_PROPERTY_REQUESTS_SENT = "requests_sent";
  private Long requestsSent;

  public static final String JSON_PROPERTY_REQUESTS_RECEIVED = "requests_received";
  private Long requestsReceived;

  public static final String JSON_PROPERTY_REQUEST_TIMEOUTS = "request_timeouts";
  private Long requestTimeouts;

  public BFTSyncMetrics requestsSent(Long requestsSent) {
    this.requestsSent = requestsSent;
    return this;
  }

   /**
   * Get requestsSent
   * @return requestsSent
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_REQUESTS_SENT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Long getRequestsSent() {
    return requestsSent;
  }


  @JsonProperty(JSON_PROPERTY_REQUESTS_SENT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setRequestsSent(Long requestsSent) {
    this.requestsSent = requestsSent;
  }


  public BFTSyncMetrics requestsReceived(Long requestsReceived) {
    this.requestsReceived = requestsReceived;
    return this;
  }

   /**
   * Get requestsReceived
   * @return requestsReceived
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_REQUESTS_RECEIVED)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Long getRequestsReceived() {
    return requestsReceived;
  }


  @JsonProperty(JSON_PROPERTY_REQUESTS_RECEIVED)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setRequestsReceived(Long requestsReceived) {
    this.requestsReceived = requestsReceived;
  }


  public BFTSyncMetrics requestTimeouts(Long requestTimeouts) {
    this.requestTimeouts = requestTimeouts;
    return this;
  }

   /**
   * Get requestTimeouts
   * @return requestTimeouts
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_REQUEST_TIMEOUTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Long getRequestTimeouts() {
    return requestTimeouts;
  }


  @JsonProperty(JSON_PROPERTY_REQUEST_TIMEOUTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setRequestTimeouts(Long requestTimeouts) {
    this.requestTimeouts = requestTimeouts;
  }


  /**
   * Return true if this BFTSyncMetrics object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BFTSyncMetrics bfTSyncMetrics = (BFTSyncMetrics) o;
    return Objects.equals(this.requestsSent, bfTSyncMetrics.requestsSent) &&
        Objects.equals(this.requestsReceived, bfTSyncMetrics.requestsReceived) &&
        Objects.equals(this.requestTimeouts, bfTSyncMetrics.requestTimeouts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestsSent, requestsReceived, requestTimeouts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BFTSyncMetrics {\n");
    sb.append("    requestsSent: ").append(toIndentedString(requestsSent)).append("\n");
    sb.append("    requestsReceived: ").append(toIndentedString(requestsReceived)).append("\n");
    sb.append("    requestTimeouts: ").append(toIndentedString(requestTimeouts)).append("\n");
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

