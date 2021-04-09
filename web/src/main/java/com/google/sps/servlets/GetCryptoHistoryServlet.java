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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.data.Stock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/** Servlet responsible for creating new tasks. */
@WebServlet("/get-crypto-history")
public class GetCryptoHistoryServlet extends HttpServlet {

  // The label for US Dollar Datastore Entity.
  private static final String USD_LABEL = "USD";
  // The label for the Crypto Symbol Datastore Entity.
  private static final String SYMBOL_LABEL = "Symbol";
  // The label for the URL Datastore Entity.
  private static final String URL_LABEL = "Url";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String cryptoSymbol = request.getParameter("symbol");

    // Scrapes Crypto Data from the coinmarketcap.com html.
    Document coinMarketDoc = Jsoup.connect("https://coinmarketcap.com/currencies/").get();

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
    
  }

  public static void queryDatastore(String cryptoSymbol) {
    // Query datastore to see if we already have up-to-date data. If we do, then we don't need to do any additional scraping.
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> queryCryptoHistory =
        Query.newEntityQueryBuilder()
            .setKind("CryptoHistory")
            .setOrderBy(OrderBy.desc("Date"))
            .setFilter(PropertyFilter.eq("Symbol", cryptoSymbol))
            .build();
    QueryResults<Entity> results = datastore.run(queryCryptoHistory);

    if (results.hasNext()) {
      // Check if the most recent result is from today. Result is sorted by descending order.
      Entity result = results.next();
      
    }
  }
}