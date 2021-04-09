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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    // The suffix for https://coinmarketcap.com/currencies/<cmcUrl>/
    // For example, Bitcoin: /get-crypto-history?cmcUrl=bitcoin
    String cmcUrl = request.getParameter("cmcUrl");

    // Scrapes Crypto Data from the coinmarketcap.com html.
    Document currencyDoc = Jsoup.connect("https://coinmarketcap.com/currencies/" + cmcUrl).get();
  }

  public static void queryDatastore(String cryptoSymbol) {
    // Query datastore to see if we already have up-to-date data. If we do, then we don't need to do
    // any additional scraping.
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
