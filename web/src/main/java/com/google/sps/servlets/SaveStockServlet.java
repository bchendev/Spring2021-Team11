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
import java.util.Calendar;
import java.util.Date;
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
    String websiteData = doc.html(); // prints HTML data
    System.out.println("✔️GOT HTML PAGE");

    Elements tik = doc.select(".coin-item-symbol");
    Elements price = doc.select(".price___3rj7O ");
    System.out.println("✔️GOT ALL TICKER SYMBOLS IN PAGE");
    System.out.println("✔️GOT ALL PRICES IN PAGE");

    long timeStamp = System.currentTimeMillis();

    Date rawDate = Calendar.getInstance().getTime();
    String stringDate = rawDate.toString();
    String[] date = stringDate.split(" ");

    if (date[0].equalsIgnoreCase("Sun")) {
      date[0] = "Sunday";
    } else if (date[0].equalsIgnoreCase("Mon")) {
      date[0] = "Monday";
    } else if (date[0].equalsIgnoreCase("Tue")) {
      date[0] = "Tuesday";
    } else if (date[0].equalsIgnoreCase("Wed")) {
      date[0] = "Wednesday";
    } else if (date[0].equalsIgnoreCase("Thu")) {
      date[0] = "Thursday";
    } else if (date[0].equalsIgnoreCase("Fri")) {
      date[0] = "Friday";
    } else {
      date[0] = "Saturday";
    }
    if (date[1].equalsIgnoreCase("Jan")) {
      date[1] = "January";
    } else if (date[1].equalsIgnoreCase("Feb")) {
      date[1] = "Febuary";
    } else if (date[1].equalsIgnoreCase("Mar")) {
      date[1] = "March";
    } else if (date[1].equalsIgnoreCase("Apr")) {
      date[1] = "April";
    } else if (date[1].equalsIgnoreCase("May")) {
      date[1] = "May";
    } else if (date[1].equalsIgnoreCase("Jun")) {
      date[1] = "June";
    } else if (date[1].equalsIgnoreCase("Jul")) {
      date[1] = "July";
    } else if (date[1].equalsIgnoreCase("Aug")) {
      date[1] = "August";
    } else if (date[1].equalsIgnoreCase("Sep")) {
      date[1] = "September";
    } else if (date[1].equalsIgnoreCase("Oct")) {
      date[1] = "October";
    } else if (date[1].equalsIgnoreCase("Nov")) {
      date[1] = "November";
    } else {
      date[1] = "December";
    }

    StringBuilder exactDate = new StringBuilder();
    exactDate.append(date[0]);
    exactDate.append(", ");
    exactDate.append(date[1] + " ");
    exactDate.append(date[2]);
    exactDate.append(", ");
    exactDate.append(date[5]);
    exactDate.append(" at ");
    exactDate.append(date[3]);

    String printDate = exactDate.toString();

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Stock");
    for (int i = 0; i < tik.size(); i++) {
      String tikk = tik.get(i).text();
      String pricee = price.get(i).text();
      FullEntity taskEntity =
          Entity.newBuilder(keyFactory.newKey())
              .set("Tik", tikk)
              .set("Price", pricee)
              .set("timestamp", timeStamp)
              .set("Actual_Time", printDate)
              .build();
      datastore.put(taskEntity);
    }

    response.sendRedirect("/index.html");
  }
}
