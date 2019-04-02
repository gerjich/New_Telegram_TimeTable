import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Bot extends TelegramLongPollingBot {

    public Bot() {
    }

    Map<String, Map<String, String>> groupDict = new HashMap<>(); //key - группа, value - словарь (день - расписание)
    Map<String, Long[][]> visitDict = new HashMap<>(); //key - дата, value - (номер пары - id присутствующих)
    Map<Long, User> users = new HashMap<>();
    Const constants = new Const();


    public Bot(DefaultBotOptions options) {
        super(options);
        Read read = new Read();
        groupDict = read.read();
        constants.makeConst();
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String instruction = message.getText().toLowerCase().substring(1);
            String[] instructions = instruction.split(" ");
            String text = null;
            Long chatID = message.getChatId();

            User tempUser = gerTempUser(chatID);

            if (groupDict.get("commands").containsKey(instructions[0])) {
                text = groupDict.get("commands").get(instructions[0]);
            } else if ("name".equals(instructions[0])) {
                String name = instruction.substring("name".length());
                tempUser.changeName(name);
                text = String.format("Your new name is %s", name);

            } else if ("password".equals(instructions[0])) {
                Boolean correct = tempUser.getRights(instructions[1]);
                if (correct) {
                    text = "correct password";
                } else {
                    text = "wrong password";
                }
            } else if (isGroup(instructions[0]) || (isDay(instructions[0]))) {
                text = getTimeTable(instructions, tempUser);
            } else if (("show".equals(instructions[0])) && (instructions.length == 4)) {
                text = showStudents(instructions, tempUser.Teacher);
            } else if (("present".equals(instructions[0])) && (instructions.length == 4)) {
                text = addPresent(instructions, tempUser);
            }

            if (text == null)
                text = "Wrong input";

            sendMsg(message, text);
        }
    }

    private User gerTempUser(Long chatID) {
        if (users.containsKey(chatID)) {
            return users.get(chatID);
        }
        User tempUser = new User();
        tempUser.setID(chatID);
        tempUser.changeName(Long.toString(chatID));
        users.put(chatID, tempUser);

        return tempUser;

    }


    public String showStudents(String[] instructions, Boolean isTeacher) {
        if (isDate(instructions[1]) && isGroup(instructions[2]) && isLesson(instructions[3])) {
            String date = String.format("%1$s %2$s", instructions[1], instructions[2]);

            Integer lesson = Integer.parseInt(instructions[3]);
            if (visitDict.containsKey(date)) {
                if (isTeacher) {
                    Long[] students = visitDict.get(date)[lesson];
                    String text = Long.toString(students[0]) + "\n";
                    for (int i = 1; i < students[0]; i++) {
                        text = String.format("%1$s%2$s\n", text, users.get(students[i])); //заменяет ID на имена (через класс User)
                    }
                    if (text == "") {
                        return "0 \n" + "There was no one";
                    }
                    return text;
                }
                return "Enter the password";
            }
            return "there were no lessons in " + date + " " + instructions[3];
        }
        return "wrong date format";
    }

    public String addPresent(String[] instructions, User tempUser) {
        if (isDate(instructions[1]) && isGroup(instructions[2]) && isLesson(instructions[3])) {
            String date = String.format("%1$s %2$s", instructions[1], instructions[2]);
            try {
                Integer lesson = Integer.parseInt(instructions[3]);
                if (visitDict.containsKey(date)) {
                    Long[][] students = visitDict.get(date);
                    students[lesson][0] += 1;  //Добавляем количество учеников
                    students[lesson][students[lesson][0].intValue()] = tempUser.ID; //Добавляем  ID студента
                    if (tempUser.Name == Long.toString(tempUser.ID)) {
                        return "Enter your name after \"/name\"";
                    }
                    return "You were " + date + " " + instructions[3];
                } else {
                    Long[][] students = new Long[7][200];
                    students[lesson][0] = Long.valueOf(1); //количество присутствующих
                    students[lesson][1] = tempUser.ID; // Добавляем  ID студента
                    visitDict.put(date, students);
                    if (tempUser.Name == Long.toString(tempUser.ID)) {
                        return "Enter your name after \"/name\"";
                    }
                    return "You were " + date + " " + instructions[3];
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return "wrong date format";
            }
        }
        return "wrong date format";
    }

    private String getTimeTable(String[] instructions, User tempUser) {
        String group = tempUser.Group;
        String day = tempUser.Day;
        if (isGroup(instructions[0])) {
            group = instructions[0];
            if (instructions.length == 2 && isDay(instructions[1])) {
                day = instructions[1];
            } else if (day == null) {
                return "Enter the day of week";
            }
        } else if (isDay(instructions[0])) {
            day = instructions[0];
            if (instructions.length == 2 && isGroup(instructions[1])) {
                group = instructions[1];
            } else if (group == null) {
                return "Enter the group";
            }
        }
        tempUser.changeGroup(group);
        tempUser.changeDay(day);
        try {
            String text = groupDict.get(group).get(day);
            return group + " " + day + "\n\n" + text;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private Boolean isDate(String s) {
        try {
            constants.format.parse(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isDay(String s) {
        return constants.WEEK_DAYS.contains(s);
    }

    private boolean isGroup(String s) {
        return (Pattern.matches("^\\D+-\\d+$", s));
    }

    private boolean isLesson(String s) {
        return (Pattern.matches("^\\d ?$", s));
    }

    public String getBotUsername() {
        return "ScheduleMatmehBot";
    }

    public String getBotToken() {
        return "797400700:AAH-3KwxKz6JFKNSxTIPbd1xkjWWQku1Tcs";
    }
}