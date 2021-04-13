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
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.google.gson.JsonObject;
import com.google.sps.data.CryptocurrencyHistory;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.FileWriter;
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
            System.out.println(connectUrl);
    Document currencyDoc = Jsoup.connect(connectUrl).ignoreContentType(true).get();
    String rawData = currencyDoc.body().text();
    JsonElement coinMarketHistoryJsonElement = JsonParser.parseString(rawData);
    JsonObject coinMarketHistoryCryptoNode =
        coinMarketHistoryJsonElement
            .getAsJsonObject()
            .getAsJsonObject("data");
    JsonArray coinMarketHistoryCrypoDataArray =
        coinMarketHistoryCryptoNode.getAsJsonArray("quotes");

    ArrayList<CryptocurrencyHistory> cryptoPriceHistories = new ArrayList<CryptocurrencyHistory>();
    coinMarketHistoryCrypoDataArray.forEach(
        jsonObject -> {
          JsonObject coinJson = (JsonObject) jsonObject.getAsJsonObject()
    .getAsJsonObject("quote")
    .getAsJsonObject("USD");

        String time = coinJson.get("timestamp").getAsString();

        String price = coinJson.get("close").getAsString();
        String usd = roundUsd(price);

        // System.out.println(time + "-----" + usd);
        //  System.out.println(coinJson.toString());
        CryptocurrencyHistory cryptoHistory = new CryptocurrencyHistory(time,price);
            cryptoPriceHistories.add(cryptoHistory);
          });
        Gson gson = new Gson();

        FileWriter myWriter = new FileWriter("check2.json");
        myWriter.write(coinMarketHistoryCryptoNode.toString());
        myWriter.close();  
            
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(cryptoPriceHistories));
  

  }

  // Rounds the USD to the nearest cent.
   private static String roundUsd(String usd) {
    BigDecimal bigDecimal = new BigDecimal(usd);
    bigDecimal.setScale(1, RoundingMode.HALF_UP).setScale(2);
    return bigDecimal.setScale(1, RoundingMode.HALF_UP).setScale(2).toString();
  }
}





