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


package com.radixdlt.api.core.system.openapitools.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;


/**
 * SystemConfigurationResponse
 */
@JsonPropertyOrder({
  SystemConfigurationResponse.JSON_PROPERTY_MEMPOOL,
  SystemConfigurationResponse.JSON_PROPERTY_BFT,
  SystemConfigurationResponse.JSON_PROPERTY_SYNC,
  SystemConfigurationResponse.JSON_PROPERTY_NETWORKING
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-11-26T15:21:53.191235-06:00[America/Chicago]")
public class SystemConfigurationResponse {
  public static final String JSON_PROPERTY_MEMPOOL = "mempool";
  private MempoolConfiguration mempool;

  public static final String JSON_PROPERTY_BFT = "bft";
  private BFTConfiguration bft;

  public static final String JSON_PROPERTY_SYNC = "sync";
  private SyncConfiguration sync;

  public static final String JSON_PROPERTY_NETWORKING = "networking";
  private NetworkingConfiguration networking;


  public SystemConfigurationResponse mempool(MempoolConfiguration mempool) {
    this.mempool = mempool;
    return this;
  }

   /**
   * Get mempool
   * @return mempool
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_MEMPOOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public MempoolConfiguration getMempool() {
    return mempool;
  }


  @JsonProperty(JSON_PROPERTY_MEMPOOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMempool(MempoolConfiguration mempool) {
    this.mempool = mempool;
  }


  public SystemConfigurationResponse bft(BFTConfiguration bft) {
    this.bft = bft;
    return this;
  }

   /**
   * Get bft
   * @return bft
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_BFT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public BFTConfiguration getBft() {
    return bft;
  }


  @JsonProperty(JSON_PROPERTY_BFT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setBft(BFTConfiguration bft) {
    this.bft = bft;
  }


  public SystemConfigurationResponse sync(SyncConfiguration sync) {
    this.sync = sync;
    return this;
  }

   /**
   * Get sync
   * @return sync
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_SYNC)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SyncConfiguration getSync() {
    return sync;
  }


  @JsonProperty(JSON_PROPERTY_SYNC)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSync(SyncConfiguration sync) {
    this.sync = sync;
  }


  public SystemConfigurationResponse networking(NetworkingConfiguration networking) {
    this.networking = networking;
    return this;
  }

   /**
   * Get networking
   * @return networking
  **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_NETWORKING)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NetworkingConfiguration getNetworking() {
    return networking;
  }


  @JsonProperty(JSON_PROPERTY_NETWORKING)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setNetworking(NetworkingConfiguration networking) {
    this.networking = networking;
  }


  /**
   * Return true if this SystemConfigurationResponse object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SystemConfigurationResponse systemConfigurationResponse = (SystemConfigurationResponse) o;
    return Objects.equals(this.mempool, systemConfigurationResponse.mempool) &&
        Objects.equals(this.bft, systemConfigurationResponse.bft) &&
        Objects.equals(this.sync, systemConfigurationResponse.sync) &&
        Objects.equals(this.networking, systemConfigurationResponse.networking);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mempool, bft, sync, networking);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SystemConfigurationResponse {\n");
    sb.append("    mempool: ").append(toIndentedString(mempool)).append("\n");
    sb.append("    bft: ").append(toIndentedString(bft)).append("\n");
    sb.append("    sync: ").append(toIndentedString(sync)).append("\n");
    sb.append("    networking: ").append(toIndentedString(networking)).append("\n");
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

