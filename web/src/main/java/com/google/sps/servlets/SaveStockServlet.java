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
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import java.io.IOException;
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
    // Sanitize user input to remove HTML tags and JavaScript.

    Document doc = Jsoup.connect("https://coinmarketcap.com/").get();
    String websitedata = doc.html(); // prints HTML data
    System.out.println("✔️GOT HTML PAGE");

    Elements tik = doc.select(".coin-item-symbol");
    Elements price = doc.select(".price___3rj7O ");
    System.out.println("✔️GOT ALL TICKER SYMBOLS IN PAGE");
    System.out.println("✔️GOT ALL PRICES IN PAGE");

    long timestamp = System.currentTimeMillis();

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Stock");
    for (int i = 0; i < tik.size(); i++) {
      String tikk = tik.get(i).text();
      String pricee = price.get(i).text();
      FullEntity taskEntity =
          Entity.newBuilder(keyFactory.newKey())
              .set("Tik", tikk)
              .set("Price", pricee)
              .set("timestamp", timestamp)
              .build();
      datastore.put(taskEntity);
    }

    response.sendRedirect("/index.html");
  }
}
