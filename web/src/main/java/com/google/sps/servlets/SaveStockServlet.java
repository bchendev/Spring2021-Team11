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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
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

    Elements tik = doc.select(".coin-item-symbol");
    Elements price = doc.select(".price___3rj7O ");

    long timeStamp = System.currentTimeMillis();

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Stock");
    ArrayList<String> tikkers = new ArrayList<String>();
    for (int i = 0; i < tik.size(); i++) {
      String tikk = tik.get(i).text();
      tikkers.add(tikk);
    }


    for (int i = 0; i < tik.size(); i++) {
      Key myTiks = datastore.newKeyFactory().setKind("Stock").newKey(tikkers.get(i));

      String pricee = price.get(i).text().replaceAll("[\\\\$,]", "");
      Double priceDouble = Double.parseDouble(pricee);

      Entity taskEntity =
          Entity.newBuilder(datastore.get(myTiks))
              .set("Tik", tikkers.get(i))
              .set("USD", priceDouble)
              .set("TimeStamp", timeStamp)
              .build();
      datastore.update(taskEntity);
    }

    for (int i = 0; i < tik.size(); i++) {

      String pricee = price.get(i).text().replaceAll("[\\\\$,]", "");
      Double priceDouble = Double.parseDouble(pricee);

      Query<Entity> query =
          Query.newEntityQueryBuilder()
              .setKind("Stock")
              .setFilter(PropertyFilter.eq("Tik", tikkers.get(i)))
              .build();
      QueryResults<Entity> results = datastore.run(query);

      Entity entity = results.next();

      String tiko = entity.getString("Tik");
      Long id = entity.getKey().getId();
      Key keyy = entity.getKey();

      // Was trying to figure it out but couldnt
      // Entity task = Entity.newBuilder(datastore.get(taskKey))
      //     .set("priority", 5)
      //     .build();
      // datastore.update(task);

    }


    

    response.sendRedirect("/index.html");
  }
}
