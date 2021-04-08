package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handles requests sent to the /hello URL. Try running a server and navigating to /hello! */
@WebServlet("/refreshComment")
public class LatestComments extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("text/html;");

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("Comments")
            .setOrderBy(OrderBy.desc("comment"))
            .build();
    QueryResults<Entity> savedComments = datastore.run(query);

    String comments = "";
    int commentsIndex = 0;
    while (savedComments.hasNext()) {
      Entity entity = savedComments.next();
      String comment = entity.getString("comment");
      comments += comment;
      commentsIndex++;
      if(commentsIndex == 50){
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