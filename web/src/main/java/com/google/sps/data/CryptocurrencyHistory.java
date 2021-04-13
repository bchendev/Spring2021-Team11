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
public final class CryptocurrencyHistory {
  // The symbol of the currency. ex BTC, ETH
  private final String timestamp;
  // The name of the currency. ex Bitcoin, Ethereum
  private final String price;
  public CryptocurrencyHistory(String timestamp, String price){
      this.timestamp = timestamp;
      this.price = price;
  }

  }

