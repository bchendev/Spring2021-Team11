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
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/sticker-count")
public class StickerCount extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> listStickers = new ArrayList<String>();
    List<String> repeatedStickers = new ArrayList<String>();

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("Stocks-mentioned")
            .setOrderBy(OrderBy.desc("stock"))
            .build();
    QueryResults<Entity> stockMention = datastore.run(query);

    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Sticker-count");

    while (stockMention.hasNext()) {
      Entity entity = stockMention.next();
      String stockSticker = entity.getString("stock");
      listStickers.add(stockSticker);
    }

    for (int stickerIndex = 0; stickerIndex < listStickers.size(); stickerIndex++) {
      int count = 0;
      if (!repeatedStickers.contains(listStickers.get(stickerIndex))) {
        for (int nextStickerIndex = stickerIndex;
            nextStickerIndex < listStickers.size();
            nextStickerIndex++) {
          if (listStickers.get(stickerIndex).equals(listStickers.get(nextStickerIndex))) {
            count++;
            repeatedStickers.add(listStickers.get(stickerIndex));
          }
        }
      }
      if (count != 0) {
        Key countkey =
            datastore.newKeyFactory().setKind("Stock").newKey(listStickers.get(stickerIndex));
        System.out.println("Sticker: " + listStickers.get(stickerIndex) + ", Count: " + count);
        FullEntity stickerCount =
            Entity.newBuilder(countkey)
                .set("sticker", listStickers.get(stickerIndex))
                .set("count", count)
                .build();
        datastore.put(stickerCount);
      }
    }
  }
}
