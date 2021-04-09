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

package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/** Servlet responsible for creating new tasks. */
@WebServlet("/save-crypto")
public class SaveCryptoServlet extends HttpServlet {

  // The label for US Dollar Datastore Entity.
  private static final String USD_LABEL = "USD";
  // The label for the Name Datastore Entity.
  private static final String NAME_LABEL = "Name";
  // The label for the URL Datastore Entity.
  private static final String CMC_URL_LABEL = "CoinMarketCapUrl";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Scrapes Crypto Data from the coinmarketcap.com html.
    Document coinMarketDoc = Jsoup.connect("https://coinmarketcap.com/").get();

    // The __NEXT_DATA__ html tag contains a script that holds all top 100 coin values in JSON
    // format.
    Elements coinMarketElements = coinMarketDoc.getElementsByAttributeValue("id", "__NEXT_DATA__");
    String coinMarketRawHtml = coinMarketElements.first().html();
    JsonElement coinMarketJsonElement = JsonParser.parseString(coinMarketRawHtml);
    JsonObject coinMarketCryptoNode =
        coinMarketJsonElement
            .getAsJsonObject()
            .getAsJsonObject("props")
            .getAsJsonObject("initialState")
            .getAsJsonObject("cryptocurrency");
    JsonArray coinMarketCrypoDataArray =
        coinMarketCryptoNode.getAsJsonObject("listingLatest").getAsJsonArray("data");
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Cryptocurrency");

    coinMarketCrypoDataArray.forEach(
        jsonObject -> {
          JsonObject coinJson = (JsonObject) jsonObject;
          String symbol = coinJson.get("symbol").getAsString();
          String name = coinJson.get("name").getAsString();
          String cmcUrl = coinJson.get("slug").getAsString();

          // CoinMarketCap gives conversions into BTC, ETH, and USD.
          // For our purposes, we only care about the USD price of the coin.
          JsonArray coinConversions = coinJson.getAsJsonArray("quotes");
          String usd = getUsdFromCoinConversions(coinConversions);

          Key coinEntityKey = keyFactory.newKey(symbol);
          Entity coinEntity =
              Entity.newBuilder(coinEntityKey)
                  .set(NAME_LABEL, name)
                  .set(USD_LABEL, usd)
                  .set(CMC_URL_LABEL, cmcUrl)
                  .build();
          datastore.put(coinEntity);
          System.out.println(
              String.format("Datastore Updated Crypto: %s, %s, %s, %s", symbol, name, usd, cmcUrl));
        });
  }

  // Searches the list of coin conversions for USD and returns the price.
  // An empty string is returned if no usd conversion is found.
  private static String getUsdFromCoinConversions(JsonArray coinConversions) {
    for (int i = 0; i < coinConversions.size(); i++) {
      JsonObject conversionObject = coinConversions.get(i).getAsJsonObject();
      String name = conversionObject.get("name").getAsString();
      if (name.equals(USD_LABEL)) {
        return conversionObject.get("price").getAsString();
      }
    }
    return "";
  }
}
