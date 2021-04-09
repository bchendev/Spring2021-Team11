// //Generate links to each ticker and use javascript to grab data from the files to plot each
// graph? maybe?

// package com.google.sps.servlets;

// import com.google.cloud.datastore.Datastore;
// import com.google.cloud.datastore.DatastoreOptions;
// import com.google.cloud.datastore.Entity;
// import com.google.cloud.datastore.Query;
// import com.google.cloud.datastore.QueryResults;
// import com.google.cloud.datastore.StructuredQuery.OrderBy;
// import com.google.gson.Gson;
// import com.google.sps.data.Stock;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// /** Servlet responsible for listing tasks. */
// @WebServlet("/ticker-info")
// public class ListStocksServlet extends HttpServlet {

//   @Override
//   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
// {

//     String name = Jsoup.clean(request.getParameter("ADA"), Whitelist.none());

//     Gson gson = new Gson();

//     response.setContentType("application/json;");
//     response.getWriter().println(gson.toJson(stocks));
//   }
// }
