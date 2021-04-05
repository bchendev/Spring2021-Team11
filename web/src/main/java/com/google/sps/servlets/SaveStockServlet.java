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
import java.io.FileWriter;
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

    long timeStamp = System.currentTimeMillis();

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Stock");

    ArrayList<String> tickers = new ArrayList<String>();
    for (int i = 0; i < tick.size(); i++) {
      String tik = tick.get(i).text();
      tickers.add(tik);
    }

    for (int i = 0; i < tick.size(); i++) {

      Key tickerKey = datastore.newKeyFactory().setKind("Stock").newKey(tickers.get(i));

      String tickerPrice = price.get(i).text().replaceAll("[\\\\$,]", "");
      Double priceDouble = Double.parseDouble(tickerPrice);


      String path =
          "/home/msaka/Spring2021-Team11/web/src/main/java/com/google/sps/stockData/"
              + tickers.get(i)
              + ".txt";

      FileWriter myWriter = new FileWriter(path, true);
      myWriter.write(timeStamp + "," + priceDouble + "\n");
      myWriter.close();

      Entity taskEntity =
          Entity.newBuilder(tickerKey)
              .set("Ticker", tickers.get(i))
              .set("USD", priceDouble)
              .set("TimeStamp", timeStamp)
              .build();
      datastore.put(taskEntity);
    }

    response.sendRedirect("/index.html");
  }
}
