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
@WebServlet("/graph-data")
public class GraphDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("Stock")
            .setFilter(PropertyFilter.eq("Ticker", "ADA"))
            .build();
    QueryResults<Entity> results = datastore.run(query);

    List<Stock> stocks = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();

      String id = entity.getKey().getName();
      String tickName = entity.getString("TikName");

      String tick = entity.getString("Ticker");
      double price = entity.getDouble("USD");

      Stock stock = new Stock(id, tick, price, tickName);
      stocks.add(stock);
      System.out.println("Tick: " + tick + " Price: " + price);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(stocks));
  }
}
