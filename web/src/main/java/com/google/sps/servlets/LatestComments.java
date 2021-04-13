package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/refreshComment")
public class LatestComments extends HttpServlet {
  public static String DISCUSSION_TAG = "_3cjCphgls6DH-irkVaA0GM";
  public static String COMMENTS_TAG = "_1qeIAgB0cPwnLhDF9XSiJM";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("text/html;");
    
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    Query<Entity> queryUrl =
        Query.newEntityQueryBuilder()
            .setKind("CommentsUrl")
            .setOrderBy(OrderBy.asc("timestamp"))
            .build();
    QueryResults<Entity> savedUrls = datastore.run(queryUrl);

    while (savedUrls.hasNext()) {
      Entity entity = savedUrls.next();
      String url = entity.getString("url");

      try {
        Document redditDiscussions = Jsoup.connect(url).get();

        System.out.printf("\nSuccessfully scraped Reddit Page: %s", redditDiscussions.title());

        Elements commentSection = redditDiscussions.getElementsByClass(DISCUSSION_TAG);

        for (Element comments : commentSection) {
          long timeStamp = System.currentTimeMillis();
          String comment = comments.getElementsByClass(COMMENTS_TAG).text();
          System.out.print(comment + " ");
          Key commentKey = datastore.newKeyFactory().setKind("Comment").newKey(comment);
          FullEntity redditComment =
              Entity.newBuilder(commentKey).set("comment", comment).set("timestamp",timeStamp).build();
          datastore.put(redditComment);
        }
      } catch (Exception e) {
        System.out.printf("\n%s: Failed to load url: %s", getClass().getName(), url);
      }
      break;
    }

    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("Comment")
            .setOrderBy(OrderBy.desc("timestamp"))
            .build();
    QueryResults<Entity> savedComments = datastore.run(query);

    String comments = "";
    int commentsIndex = 0;
    while (savedComments.hasNext()) {
      Entity entity = savedComments.next();
      String comment = entity.getString("comment");
      comment += "|";
      comments = comment + comments;
      commentsIndex++;
      if(commentsIndex == 20){
        break;
      }
    }
    String jsonQuestions = convertToJsonUsingGson(comments);
    response.getWriter().println(jsonQuestions);
  }

  private String convertToJsonUsingGson(String comment) {
    Gson gson = new Gson();
    String jsonQuestions = gson.toJson(comment);
    return jsonQuestions;
  }
}
