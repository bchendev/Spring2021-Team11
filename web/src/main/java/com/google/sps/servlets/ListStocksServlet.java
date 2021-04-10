package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
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
@WebServlet("/list-stock")
public class ListStocksServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind("AllCrypto")
            .setOrderBy(OrderBy.desc("TimeStamp"))
            .build();
    QueryResults<Entity> results = datastore.run(query);

    List<Stock> stocks = new ArrayList<Stock>();
    while (results.hasNext()) {
      Entity entity = results.next();

      String id = entity.getKey().getName();
      String tick = entity.getString("Ticker");
      double price = entity.getDouble("USD");
      String tickerName = entity.getString("TikName");

      Stock stock = new Stock(id, tick, price, tickerName);
      stocks.add(stock);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(stocks));
  }
}
