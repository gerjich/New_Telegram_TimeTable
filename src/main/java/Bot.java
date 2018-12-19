import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {

    public Bot() {
    }

    Map<String, Map<String, String>> groupDict = new HashMap<>(); //key - группа, value - словарь (день - расписание)
    ArrayList<User> users = new ArrayList<>();
    ArrayList<String> week = new ArrayList<>();


    public Bot(DefaultBotOptions options) {
        super(options);
        Read read = new Read();
        groupDict = read.read();
        week.addAll(Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "sunday"));
    }

    public void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try{
            execute(sendMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String instruction = message.getText().toLowerCase();
            String[] instructions = instruction.split(" ");
            String text = null;
            String group = null;
            String day = null;
            User tempUser = null;
            Long chatID = message.getChatId();

            for (User user:users) {
                if (user.ID.equals(chatID)) {
                    tempUser = user;
                    group = tempUser.Group;
                    day = tempUser.Day;
                    break;
                }
            }
            if (tempUser == null){
                tempUser = new User();
                tempUser.setID(chatID);
                users.add(tempUser);
            }

            if (groupDict.get("commands").containsKey(instructions[0].substring(1))){
                text = groupDict.get("commands").get(instructions[0].substring(1));
            }
            else if (isGroup(instructions[0])) {
                group = instructions[0].substring(1);
                if (day == null ) {
                    text = "Enter the day of week";
                }
                else {
                    if (instructions.length == 2 && isDay(instructions[1])) {
                        day = instructions[1];
                    }
                }
            }else if (isDay(instructions[0])){
                day = instructions[0].substring(1);
                if (group == null) {
                    text = "Enter the group";
                } else {
                    if (instructions.length == 2 && isGroup(instructions[1])) {
                        group = instructions[1];
                    }
                }
            }

            if (text == null) {
                try {
                    text = groupDict.get(group).get(day);
                } catch (Exception ex) {
                    text = null;
                    System.out.println(ex.getMessage());
                }
                if (text != null)
                    text = group+" "+day+"\n\n"+text;
            }

            if (text == null)
                text = "Are you sure?";
            tempUser.changeGroup(group);
            tempUser.changeDay(day);

            sendMsg(message, text);
        }
    }

    public boolean isDay(String s){
        if (s == null )
            return false;
        return (week.contains(s.substring(1)));
    }

    public boolean isGroup(String s){
        if (s == null )
            return false;
        return (Pattern.matches("^/\\D+-\\d+$", s));
    }

    public String getBotUsername() {
        return "ScheduleMatmehBot";
    }

    public String getBotToken() {
        return "797400700:AAH-3KwxKz6JFKNSxTIPbd1xkjWWQku1Tcs";
    }
}
