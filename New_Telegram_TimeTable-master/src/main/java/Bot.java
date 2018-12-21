import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {


    private Map<String, Map<String, String>> GROUP_DICT = new HashMap<>();
    //key - группа, value - словарь (день - расписание)
    private Map<Long, User> USERS = new HashMap<>();
    private ArrayList<String> WEEK_DAYS = new ArrayList<>();
    private String TEXT = null;


    public Bot(DefaultBotOptions options) {
        super(options);
        Reader reader = new Reader();
        GROUP_DICT = reader.read();
        WEEK_DAYS.addAll(Arrays.asList("monday", "tuesday", "wednesday",
                                  "thursday", "friday", "sunday"));
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
            User tempUser = null;
            Long chatID = message.getChatId();

            if (USERS.containsKey(chatID))
                tempUser = USERS.get(chatID);
            else{
                tempUser = new User();
                tempUser.setId(chatID);
                USERS.put(chatID, tempUser);
            }

            createText(instructions, tempUser);

            sendMsg(message, TEXT);
        }
    }

    public void createText (String[] instructions, User tempUser){
        TEXT = null;

        if (GROUP_DICT.get("commands").containsKey(instructions[0].substring(1))){
            TEXT = GROUP_DICT.get("commands").get(instructions[0].substring(1));
        }
        else if (isGroup(instructions[0])) {
            changeTextForGroup(instructions, tempUser);

        }else if (isDay(instructions[0])){
            changeTextForDay(instructions, tempUser);
        }

        if (TEXT == null && tempUser.day != null && tempUser.group != null) {
            try {
                TEXT = tempUser.group +" "+tempUser.day +"\n\n"+
                        GROUP_DICT.get(tempUser.group).get(tempUser.day);
            } catch (Exception ex) {
                TEXT = null;
                System.out.println(ex.getMessage());
            }
        }

        if (TEXT == null){
            TEXT = "Are you sure?";
        }
    }

    public void changeTextForGroup(String[] instructions, User tempUser) {
        tempUser.group = instructions[0].substring(1);
        if (tempUser.day == null) {
            TEXT = "Enter the day of week";
        } else {
            if (instructions.length == 2 && isDay(instructions[1])) {
                tempUser.day = instructions[1];
            }
        }
    }

    public void changeTextForDay(String[] instructions, User tempUser){
        tempUser.day = instructions[0].substring(1);
        if (tempUser.group == null) {
            TEXT = "Enter the group";
        } else {
            if (instructions.length == 2 && isGroup(instructions[1])) {
                tempUser.group = instructions[1];
            }
        }
    }

    public boolean isDay(String s){
        return (WEEK_DAYS.contains(s.substring(1)));
    }

    public boolean isGroup(String s){
        return (Pattern.matches("^/\\D+-\\d+$", s));
    }

    public String getBotUsername() {
        return "ScheduleMatmehBot";
    }

    public String getBotToken() {
        return "797400700:AAH-3KwxKz6JFKNSxTIPbd1xkjWWQku1Tcs";
    }
}
