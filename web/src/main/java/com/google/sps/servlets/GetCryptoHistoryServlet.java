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

import java.io.IOException;
import java.util.Calendar;
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

  // A string format that access the web api for crypto history.
  // Param 1: crypto id
  // Param 2: time start
  // Param 3: time end
  private static final String cmcWebApiHistoryFormat =
      "https://web-api.coinmarketcap.com/v1/cryptocurrency/ohlcv/historical?id=%s&convert=USD&time_start=%s&time_end=%s";
  // change web to pro? different error
  private static final int DAYS_IN_YEAR = 365;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String cmcId = request.getParameter("cmcId");

    // Get data from up to 1 year from today.
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR, -DAYS_IN_YEAR);

    String connectUrl =
        String.format(
            cmcWebApiHistoryFormat, cmcId, cal.getTimeInMillis(), System.currentTimeMillis());
    Document currencyDoc = Jsoup.connect(connectUrl).ignoreContentType(true).get();
    response.setContentType("application/json;");
    response.getWriter().println(currencyDoc.body().text());
    System.out.println(currencyDoc.body().text());
  }
}
