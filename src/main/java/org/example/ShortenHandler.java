package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

class ShortenHandler implements HttpHandler {

    private SimpleDatabase simpleDatabase;
    public ShortenHandler(SimpleDatabase simpleDatabase) {
        this.simpleDatabase = simpleDatabase;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("=")) {
                //Дані
                String data = query.substring(query.indexOf("=") + 1);
                String redirectUrl = simpleDatabase.getFullUrl(data);
                simpleDatabase.incrementClicks(data);

                // Відправити статус 302 Redirect та встановити заголовок Location з новою URL
                exchange.getResponseHeaders().set("Location", redirectUrl);
                exchange.sendResponseHeaders(302, -1);
            } else {
                // Bad request - no data provided
                exchange.sendResponseHeaders(400, -1);
            }
        } else {
            // Method not allowed
            exchange.sendResponseHeaders(405, -1);
        }
    }

}