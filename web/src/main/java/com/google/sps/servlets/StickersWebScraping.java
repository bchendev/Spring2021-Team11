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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/store-stickers")
public class StickersWebScraping extends HttpServlet {
  private List<String> stockStickers = new ArrayList<String>();
  public static String stocksWebPage;
  public static String nasdaqStocks;
  public static String QUOTES_TAG = "quotes";
  public static String STICKERS_GROUP1_TAG = "ro";
  public static String STICKERS_GROUP2_TAG = "re";
  public static String STICKER_TAG = "a";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get all stock stickers listed in NYSE and NASDAQ, divided by alphabetical order
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Stickers");

    // Generate the link for each alphabetical group
    for (char letter = 'A'; letter <= 'Z'; ++letter) {
      stocksWebPage = "https://eoddata.com/stocklist/NYSE/" + letter + ".htm";
      nasdaqStocks = "https://eoddata.com/stocklist/NASDAQ/" + letter + ".htm";
      Document docNyse = Jsoup.connect(stocksWebPage).get();
      Document docNasdaq = Jsoup.connect(nasdaqStocks).get();

      Elements stocksNyse = docNyse.getElementsByClass(QUOTES_TAG);
      Elements stocksNasdaq = docNasdaq.getElementsByClass(QUOTES_TAG);

      addStickers(stocksNyse);
      addStickers(stocksNasdaq);
    }
    for (int i = 0; i < stockStickers.size(); i++) {
      System.out.print(stockStickers.get(i)+" ");
      FullEntity saveStickers =
          Entity.newBuilder(keyFactory.newKey()).set("sticker", stockStickers.get(i)).build();
      datastore.put(saveStickers);
    }
    response.sendRedirect("/index.html");
  }

  public void addStickers(Elements stocks) {
    for (Element stock : stocks) {
      Elements stockGeneralInfo1 = stock.getElementsByClass(STICKERS_GROUP1_TAG);
      Elements stockGeneralInfo2 = stock.getElementsByClass(STICKERS_GROUP2_TAG);
      for (Element sticker : stockGeneralInfo1) {
        String sticker1 = sticker.select(STICKER_TAG).text();
        sticker1 = sticker1.trim();
        if (!stockStickers.contains(sticker1)) {
          stockStickers.add(sticker1);
        }
      }
      for (Element sticker : stockGeneralInfo2) {
        String sticker2 = sticker.select(STICKER_TAG).text();
        sticker2 = sticker2.trim();
        if (!stockStickers.contains(sticker2)) {
          stockStickers.add(sticker2);
        }
      }
    }
  }
}
