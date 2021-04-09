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
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/** Servlet responsible for creating new tasks. */
@WebServlet("/save-stock")
public class SaveStockServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Scrapes Cryptocurrency data
    Document doc = Jsoup.connect("https://coinmarketcap.com/").get();
    String websiteData = doc.html();

    Elements tick = doc.select(".coin-item-symbol");
    Elements price = doc.select(".price___3rj7O ");

    Elements otherTick = doc.select(".crypto-symbol");
    Elements tableRows =
        doc.select("table.cmc-table.cmc-table___11lFC.cmc-table-homepage___2_guh > tbody > tr");

    Elements name = doc.select("p.sc-AxhUy.fqrLrs");
    int otherNames = 90;

    long timeStamp = System.currentTimeMillis();

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    ArrayList<String> tickers = new ArrayList<String>();
    ArrayList<Double> prices = new ArrayList<Double>();
    ArrayList<String> names = new ArrayList<String>();

    for (int i = 0; i < name.size(); i++) {
      String tickName = name.get(i).text();
      names.add(tickName);
    }

    for (int i = 0; i < otherNames; i++) {
      String otherTickName = "lol";
      names.add(otherTickName);
    }

    // First 10
    for (int i = 0; i < tick.size(); i++) {
      String tik = tick.get(i).text();
      tickers.add(tik);
    }
    // Last 90
    for (int i = 0; i < otherTick.size(); i++) {
      String othertik = otherTick.get(i).text();
      tickers.add(othertik);
    }

    for (int i = 0; i < price.size(); i++) {
      String tickerPrice = price.get(i).text().replaceAll("[\\\\$,]", "");
      Double priceDouble = Double.parseDouble(tickerPrice);
      prices.add(priceDouble);
    }

    for (int i = 10; i < tableRows.size(); i++) { // first row is the col names so skip it.
      String str = tableRows.get(i).text();
      String wow = str.substring(str.indexOf('$') + 1).trim();
      double lopl = Double.parseDouble(wow);
      prices.add(lopl);
    }

    for (int i = 0; i < tickers.size(); i++) {
      Key tickerKey = datastore.newKeyFactory().setKind("AllCrypto").newKey(tickers.get(i));

      Entity taskEntity =
          Entity.newBuilder(tickerKey)
              .set("TikName", names.get(i))
              .set("Ticker", tickers.get(i))
              .set("USD", prices.get(i))
              .set("TimeStamp", timeStamp)
              .build();
      datastore.put(taskEntity);
    }

    response.sendRedirect("/index.html");
  }
}
