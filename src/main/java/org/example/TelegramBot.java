package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBot extends TelegramLongPollingBot {
    private SimpleDatabase simpleDatabase;
    public TelegramBot(SimpleDatabase simpleDatabase) {
        this.simpleDatabase = simpleDatabase;
    }

    @Override
    public String getBotUsername() {
        return "https://t.me/LinkMasterHackathonBot";
    }

    @Override
    public String getBotToken() {
        return "7036659834:AAH5OjX9znbGtVtlgD2epUXzW2jTcezK9c8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обробляйте вхідні повідомлення тут
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            if (messageText.startsWith("/reduction_link")) {
                String response = reductionLink(update);
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(response);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (messageText.startsWith("/help")) {
                String helpMessage = "Список команд:\n" +
                        "/help - вивести список команд\n" +
                        "/info - отримати інформацію про доступні посилання\n" +
                        "/reduction_link - скоротити введене посилання\n" +
                        "/delete_link - видалити певне посилання\n" +
                        "/be_happy - просто бути щасливим";
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(helpMessage);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (messageText.startsWith("/info")) {
                System.out.println(update.getMessage().getFrom().getUserName());
                String infoMessage = simpleDatabase.getUserStatistics(update.getMessage().getFrom().getUserName());
                System.out.println(infoMessage);
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(infoMessage);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (messageText.startsWith("/delete_link")) {
                // Реалізуйте логіку для команди /delete_link тут
            } else if (messageText.equals("/be_happy")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("https://www.youtube.com/watch?v=ewkycTRfvx4");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private String reductionLink(Update update){
        String message = update.getMessage().getText();
        String link = message.replace("/reduction_link ","");
        String shortLink = simpleDatabase.shortenURL(message);
        String user = update.getMessage().getFrom().getUserName();
        simpleDatabase.addURL(link,shortLink,user);

        String returnLink = "http://localhost:8000/?=" + shortLink;
        return "Успішно, ваше скорочене посилання: \n" + returnLink;
    }

    public void start() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(this);
            System.out.println("Бот запущений. Він готовий приймати повідомлення.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
