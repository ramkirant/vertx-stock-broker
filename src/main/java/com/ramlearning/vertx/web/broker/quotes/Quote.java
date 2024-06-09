package com.ramlearning.vertx.web.broker.quotes;

import com.ramlearning.vertx.web.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Quote {

  Asset asset;
  Integer bid;
  Integer ask;
  Integer lastPrice;
  Integer volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
