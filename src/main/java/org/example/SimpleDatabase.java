package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class SimpleDatabase {
    // Змінна для зберігання об'єкту з'єднання з базою даних
    private Connection connection;

    // Конструктор класу
    public SimpleDatabase(String dbFilePath) {
        try {
            // З'єднання з базою даних SQLite
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для ініціалізації бази даних
    public void initializeDatabase() {
        if (connection != null) {
            try {
                // Створення таблиці, якщо вона ще не існує
                String createTableSQL = "CREATE TABLE IF NOT EXISTS urls (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "full_url TEXT," +
                        "short_url TEXT UNIQUE," + // Забезпечує унікальність скорочених посилань
                        "clicks INTEGER," +
                        "owner_name TEXT)";
                connection.createStatement().executeUpdate(createTableSQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для додавання запису в базу даних
    public void addURL(String fullUrl, String shortUrl, String ownerName) {
        if (connection != null) {
            try {
                // SQL-запит для вставки даних
                String insertSQL = "INSERT INTO urls (full_url, short_url, clicks, owner_name) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
                preparedStatement.setString(1, fullUrl);
                preparedStatement.setString(2, shortUrl);
                preparedStatement.setInt(3, 0);
                preparedStatement.setString(4, ownerName);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для отримання повного посилання за його скороченням
    public String getFullUrl(String shortUrl) {
        String fullUrl = null;
        if (connection != null) {
            try {
                // SQL-запит для вибору повного посилання за скороченням
                String selectSQL = "SELECT full_url FROM urls WHERE short_url = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                preparedStatement.setString(1, shortUrl);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    fullUrl = resultSet.getString("full_url");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return fullUrl;
    }

    // Метод для збільшення кількості переходів на +1
    public void incrementClicks(String shortUrl) {
        if (connection != null) {
            try {
                // SQL-запит для оновлення кількості переходів
                String updateSQL = "UPDATE urls SET clicks = clicks + 1 WHERE short_url = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                preparedStatement.setString(1, shortUrl);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для отримання статистики користувача за його ім'ям
    public String getUserStatistics(String ownerName) {
        StringBuilder statistics = new StringBuilder();
        if (connection != null) {
            try {
                // SQL-запит для вибору посилань та кількості переходів користувача
                String selectSQL = "SELECT short_url, clicks FROM urls WHERE owner_name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                preparedStatement.setString(1, ownerName);
                ResultSet resultSet = preparedStatement.executeQuery();
                statistics.append("Статистика ").append(ownerName).append(":\n");
                while (resultSet.next()) {
                    String shortUrl = resultSet.getString("short_url");
                    int clicks = resultSet.getInt("clicks");
                    statistics.append(shortUrl).append(": ").append(clicks).append(" переходів\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statistics.toString();
    }

    // Метод для видалення запису за скороченням посилання
    public void deleteURL(String shortUrl) {
        if (connection != null) {
            try {
                // SQL-запит для видалення запису
                String deleteSQL = "DELETE FROM urls WHERE short_url = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
                preparedStatement.setString(1, shortUrl);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для закриття з'єднання з базою даних
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String shortenURL(String longURL) {
        StringBuilder shortURL = new StringBuilder();
        int length = Integer.parseInt(ConfigLoader.getValue("DEFAULT_CODE_LENGTH"));

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ConfigLoader.getValue("ALLOWED_CHARACTERS").length());
            shortURL.append(ConfigLoader.getValue("ALLOWED_CHARACTERS").charAt(randomIndex));
        }

        return shortURL.toString();
    }
}
