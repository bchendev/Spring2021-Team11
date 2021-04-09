// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

/** Represents a cryptocurrency such as Bitcoin, Ethereum, etc... */
public final class Cryptocurrency {
  // The symbol of the currency. ex BTC, ETH
  private final String symbol;
  // The name of the currency. ex Bitcoin, Ethereum
  private final String name;
  // The crypto id used by coinmarketcap.com to access their web apis.
  private final String cmcId;
  // The price of the crypto in usd.
  private final String usd;
  // The crypto rank according to coinmarketcap.com
  private final String cmcRank;

  public static Cryptocurrency.Builder newBuilder() {
    return new Cryptocurrency.Builder();
  }

  public String getSymbol() {
    return symbol;
  }

  public String getName() {
    return name;
  }

  public String getCmcId() {
    return cmcId;
  }

  public String getUsd() {
    return usd;
  }

  public String getCmcRank() {
    return cmcRank;
  }

  public String toString() {
    return String.format("%s, %s, $%s, id:%s, rank:%s", name, symbol, usd, cmcId, cmcRank);
  }

  public Entity toDatastoreEntity(KeyFactory keyFactory) {
    Key coinEntityKey = keyFactory.newKey(symbol);
    return Entity.newBuilder(coinEntityKey)
        .set("Name", name)
        .set("USD", usd)
        .set("CoinMarketCapId", cmcId)
        .set("CoinMarketCapRank", cmcRank)
        .build();
  }

  private Cryptocurrency(String symbol, String name, String cmcId, String usd, String cmcRank) {
    this.symbol = symbol;
    this.name = name;
    this.cmcId = cmcId;
    this.usd = usd;
    this.cmcRank = cmcRank;
  }

  public static class Builder {
    private String symbol;
    private String name;
    private String cmcId;
    private String usd;
    private String cmcRank;

    public Builder setSymbol(String symbol) {
      this.symbol = symbol;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setCmcId(String cmcId) {
      this.cmcId = cmcId;
      return this;
    }

    public Builder setUsd(String usd) {
      this.usd = usd;
      return this;
    }

    public Builder setCmcRank(String cmcRank) {
      this.cmcRank = cmcRank;
      return this;
    }

    public Cryptocurrency build() {
      return new Cryptocurrency(symbol, name, cmcId, usd, cmcRank);
    }
  }
}
