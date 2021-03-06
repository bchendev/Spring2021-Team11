package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.sps.data.Stock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet responsible for listing tasks. */
@WebServlet("/latest-info")
public class QueryLatestInfo extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String cmcId = request.getParameter("cmcId");
    


    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder().setKind("Cryptocurrency")
        .setFilter(PropertyFilter.eq("CoinMarketCapId", cmcId))
        .build();
        QueryResults<Entity> results = datastore.run(query);
    ArrayList<Stock> stocks = new ArrayList<Stock>();
    while (results.hasNext()) {
      Entity entity = results.next();

      String id = entity.getKey().getName();
      String tickName = entity.getString("Name");
        String price = entity.getString("USD");
      String tick = entity.getString("CoinMarketCapId");

      Stock stock = new Stock(id, tickName, price, tick);

      stocks.add(stock);
      System.out.println(id + "\n " + tickName + "\n " + tick + "\n " + price + "\n ");
    }
    
    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(stocks));
  }
  
}
