import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {

    public Bot() {
    }

    Map<String, Map<String, String>> groupDict = new HashMap<>(); //key - группа, value - словарь (день - расписание)
    Map<String, Map<String, String>> users = new HashMap<>(); //key - группа, value - словарь (день - расписание)


    public Bot(DefaultBotOptions options) {
        super(options);
        Read read = new Read();
        groupDict = read.read();

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
        if (message != null && message.hasText()) { //В чём разница?
            String instruction = message.getText().toLowerCase();
            String[] instructions = instruction.split(" ");
            String text = null;
            String group = null;
            String day = null;
            if (Pattern.matches("^/\\D+-\\d+$",instructions[0])) {
                group = instructions[0].substring(1);
                if (day == null && instructions.length != 2) {
                    text = "Enter the day of week";
                }
                else {
                    if (instructions.length == 2) {
                        day = instructions[1];
                    }
                    try {
                        text = groupDict.get(group).get(day);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }else if (Pattern.matches("^/\\w+$",instructions[0])) {
                if (instructions[0].equals("/help"))
                    text = groupDict.get("commands").get(instructions[0].substring(1));
                else {
                    day = instructions[0].substring(1);
                    if (group == null && instructions.length != 2) { // problem should be : group == null
                        text = "Enter the group";
                    } else {
                        if (instructions.length == 2) {
                            group = instructions[1];
                        }
                        try {
                            text = groupDict.get(group).get(day);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }

            else
            if (text == null)
                text = "Are you sure?";
            sendMsg(message, text);
        }


    }

    public String getBotUsername() {
        return "ScheduleMatmehBot";
    }

    public String getBotToken() {
        return "0";
    }
}
