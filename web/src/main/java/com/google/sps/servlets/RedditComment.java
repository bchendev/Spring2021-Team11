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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/store-comments")
public class RedditComment extends HttpServlet {
  public static String DISCUSSION_TAG = "_3cjCphgls6DH-irkVaA0GM";
  public static String COMMENTS_TAG = "_1qeIAgB0cPwnLhDF9XSiJM";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("CommentsUrl")
            .setOrderBy(OrderBy.desc("url"))
            .build();
    QueryResults<Entity> savedUrls = datastore.run(query);

    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Comments");

    while (savedUrls.hasNext()) {
      Entity entity = savedUrls.next();
      String url = entity.getString("url");

      try {
        Document redditDiscussions = Jsoup.connect(url).get();

        System.out.printf("\nSuccessfully scraped Reddit Page: %s", redditDiscussions.title());

        Elements commentSection = redditDiscussions.getElementsByClass(DISCUSSION_TAG);

        for (Element comments : commentSection) {
          String comment = comments.getElementsByClass(COMMENTS_TAG).text();
          FullEntity redditComment =
              Entity.newBuilder(keyFactory.newKey()).set("comment", comment).build();
          datastore.put(redditComment);
        }
      } catch (Exception e) {
        System.out.printf("\n%s: Failed to load url: %s", getClass().getName(), url);
      }
    }
  }
}
