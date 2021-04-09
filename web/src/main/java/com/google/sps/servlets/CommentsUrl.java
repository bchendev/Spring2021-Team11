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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Get the urls of the latest deiscussion sections from reddit wallstreet bets
@WebServlet("/store-comments-urls")
public class CommentsUrl extends HttpServlet {
  public static String REDDIT_DISCUSSION_URL =
      "https://www.reddit.com/r/wallstreetbets/search?q=flair_name%3A%22Daily%20Discussion%22&restrict_sr=1&sort=new";
  public static String DAILY_DISCUSSION_CLASS_TAG = "SQnoC3ObvgnGjWt90zD9Z _2INHSNB8V5eaWp4P0rY_mE";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Datastore dataStore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = dataStore.newKeyFactory().setKind("Comments-url");

    Document redditPage = Jsoup.connect(REDDIT_DISCUSSION_URL).get();
    System.out.printf("Successfully scraped Reddit Page: %s", redditPage.title());

    // Get the links for the daily discussion
    Elements dailyDiscussion = redditPage.getElementsByClass(DAILY_DISCUSSION_CLASS_TAG);
    Elements links = dailyDiscussion.select("a[href]");
    System.out.println("\nURls scraped: " + links.size());

    for (Element link : links) {
      String url = "https://www.reddit.com" + link.attr("href");

      FullEntity commentsUrls = Entity.newBuilder(keyFactory.newKey()).set("url", url).build();
      System.out.println("URL:" + url);
      dataStore.put(commentsUrls);
    }
    response.sendRedirect("/index.html");
  }
}
