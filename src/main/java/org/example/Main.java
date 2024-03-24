package org.example;


import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Main {

        public static void main(String[] args) throws Exception {
            System.out.println("Підключення до бази даних");
            SimpleDatabase simpleDatabase = new SimpleDatabase("main.db");
            System.out.println("Ініціалізація бази даних");
            simpleDatabase.initializeDatabase();

            System.out.println("Ініціалізація Telegram бота");
            TelegramBot bot = new TelegramBot(simpleDatabase);
            bot.start();

            System.out.println("Ініціалізація сервера");
            HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(ConfigLoader.getValue("PORT"))), 0);
            HttpHandler handler = new ShortenHandler(simpleDatabase);
            server.createContext("/", handler);
            Thread serverThread = new Thread(() -> {
                server.start();
            });
            serverThread.start();

        }

}



