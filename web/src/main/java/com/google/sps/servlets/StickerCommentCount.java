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

@WebServlet("/reddit-count")
public class StickerCommentCount extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> listStickers = new ArrayList<String>();
    List<String> listComments = new ArrayList<String>();

    // List containig the stickers that appear on the comments
    List<String> stockMentioned = new ArrayList<String>();
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    // Get stickers
    Query<Entity> queryStickers =
        Query.newEntityQueryBuilder()
            .setKind("Stickers")
            .setOrderBy(OrderBy.desc("sticker"))
            .build();
    QueryResults<Entity> savedStickers = datastore.run(queryStickers);
    while (savedStickers.hasNext()) {
      Entity entity = savedStickers.next();
      String stock = entity.getString("sticker");
      listStickers.add(stock);
    }
    System.out.println("\nNumber of stickers loaded: " + listStickers.size());

    // Get comments
    Query<Entity> queryComments =
        Query.newEntityQueryBuilder()
            .setKind("Comments")
            .setOrderBy(OrderBy.desc("comment"))
            .build();
    QueryResults<Entity> savedComments = datastore.run(queryComments);
    while (savedComments.hasNext()) {
      Entity entity = savedComments.next();
      String comment = entity.getString("comment");
      listComments.add(comment);
    }
    System.out.println("Number of comments loaded: " + listComments.size());

    // Reads and stores the stickers that appear on the comments
    for (int commentsIndex = 0; commentsIndex < listComments.size(); commentsIndex++) {
      String comment = listComments.get(commentsIndex).trim();
      String[] commentWords = comment.split(" ");
      for (int wordIndex = 0; wordIndex < commentWords.length; wordIndex++) {
        String currWord = commentWords[wordIndex].trim();
        for (int stikcerIndex = 0; stikcerIndex < listStickers.size(); stikcerIndex++) {
          String currSticker = listStickers.get(stikcerIndex);
          if (currWord.toLowerCase().equals(currSticker.toLowerCase())) {
            stockMentioned.add(currSticker);
          }
        }
      }
    }
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("StocksMentioned");
    for (String stock : stockMentioned) {
      System.out.print(stock + " ");
      FullEntity mentionedStocks =
          Entity.newBuilder(keyFactory.newKey()).set("stock", stock).build();
      datastore.put(mentionedStocks);
    }
    response.sendRedirect("/index.html");
  }
}
