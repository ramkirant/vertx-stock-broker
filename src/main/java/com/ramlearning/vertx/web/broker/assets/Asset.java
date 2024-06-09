package com.ramlearning.vertx.web.broker.assets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

/*
 * @Value annotation will create an immutable class for us. It will create a constructor and a getter for all fields
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
  String name;
}
