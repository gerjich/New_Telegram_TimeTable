import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {

    public Bot() {
    }

    Map<String, Map<String, String>> groupDict = new HashMap<>(); //key - группа, value - словарь (день - расписание


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
        //Map<String, String> scheduleDictionaryKN201 = new HashMap<>(); //key - день, value - расписание
        /*Map<String, Map<String, String>> groupDict = new HashMap<>(); //key - группа, value - словарь (день - расписание)
        Map<String, String> oneStringCommands = new HashMap<>();
        Read read = new Read();
        groupDict = read.read();
        System.out.println(groupDict);
        */

        /*scheduleDictionaryKN201.put("monday", "schedule" );
        groupDict.put("/kn-201", scheduleDictionaryKN201);
        oneStringCommands.put("/help", "help");*/
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String instruction = message.getText().toLowerCase();
            String[] instructions = instruction.split(" ");
            String text = null;
            if (instructions.length == 1) {
                text = groupDict.get("commands").get(instructions[0].substring(1));
            } else if (instructions.length == 2) {
                String group = instructions[0];
                String day = instructions[1];
                try {
                    text = groupDict.get(group.substring(1)).get(day);
                }
                catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

            }
            if (text == null)
                text = "Are you sure?";
            sendMsg(message, text);
        }


    }

    public String getBotUsername() {
        return "ScheduleMatmehBot";
    }

    public String getBotToken() {
        return "797400700:AAH-3KwxKz6JFKNSxTIPbd1xkjWWQku1Tcs";
    }
}
