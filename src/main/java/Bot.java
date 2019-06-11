import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Bot extends TelegramLongPollingBot {

    public Bot() {
    }

    Map<String, TimeTable> timeTable = new HashMap<>(); //key - группа, value - словарь (день - расписание)
    Map<String, Log> visitLog = new HashMap<>(); //key - дата, value - (номер пары - id присутствующих)
    Map<Long, User> users = new HashMap<>();
    StringConst strConst = new StringConst();


    private TimeTable readOneFile (File fileName) {
        TimeTable tt = new TimeTable();
        try (FileReader fr = new FileReader(fileName);
             Scanner scan = new Scanner(fr))
        {
            String key = "";
            String value = "";
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.startsWith("/")) {
                    if (key != "") {
                        tt.groupDict.put(key, value);
                    }
                    key = line.substring(1);
                    value = "";
                }
                else {
                    value = String.format(strConst.addedLineForm, value, line);
                }
            }
            if (key != "") {
                tt.groupDict.put(key, value);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return tt;
    }

    private Map<String, TimeTable> read() {
        Map<String, TimeTable> bigDict = new HashMap<>();
        File dir = new File(strConst.fileNames);
        File[] files = dir.listFiles();
        try {
            for (File file:files) {
                bigDict.put(file.getName().substring(0, file.getName().length() - 4), readOneFile(file));
            }
        }

        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return bigDict;
    }


    public Bot(DefaultBotOptions options) {
        super(options);
        timeTable = read();
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
        if (message != null && message.hasText() && message.getText().startsWith("/")) {
            String instruction = message.getText().toLowerCase().substring(1);
            String[] instructions = instruction.split(" ");
            String text = null;
            Long chatID = message.getChatId();

            User tempUser = gerTempUser(chatID);

            if (instructions[0].equals(strConst.help)) {
                TimeTable tt = timeTable.get(strConst.commands);
                text = tt.groupDict.get(instructions[0]);
            } else if (strConst.name.equals(instructions[0])) {
                String name = instruction.substring(strConst.name.length());
                tempUser.changeName(name);
                text = String.format(strConst.newNameFotm, name);

            } else if (strConst.password.equals(instructions[0])) {
                Boolean correct = tempUser.getRights(instructions[1]);
                if (correct) {
                    text = strConst.correctPassword;
                } else {
                    text = strConst.wrongPassword;
                }
            } else if (isGroup(instructions[0]) || (isDay(instructions[0]))) {
                text = getTimeTable(instructions, tempUser);
            } else if ((strConst.show.equals(instructions[0])) && (instructions.length == 4)) {
                text = showStudents(instructions, tempUser.teacher);
            } else if ((strConst.present.equals(instructions[0])) && (instructions.length == 4)) {
                text = addPresent(instructions, tempUser);
            }

            if (text == null)
                text = strConst.wrongIn;

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
        if (!isTeacher) {
            return strConst.enterPass;
        }
        if (!(isDate(instructions[1]) && isGroup(instructions[2]) && isLesson(instructions[3]))) {
            return strConst.wrongInForm;
        }

        String date = String.format(strConst.dateGroupForm, instructions[1], instructions[2]);
        Integer lesson = Integer.parseInt(instructions[3]);
        if (!visitLog.containsKey(date)) {
            return String.format(strConst.noLessonForm, date);
        }
        Log log = visitLog.get(date);
        if (!log.lessonNumber.containsKey(lesson)) {
            return String.format(strConst.noLessonForm, date);
        }
        String text = "";
        for (Long l:log.lessonNumber.get(lesson)){
            text = String.format(strConst.addedLineForm, text, users.get(l).name);
        }
        return text;
    }


    public String addPresent(String[] instructions, User tempUser) {
        if (!(isDate(instructions[1]) && isGroup(instructions[2]) && isLesson(instructions[3]))) {
            return strConst.wrongInForm;
        }
        String date = String.format(strConst.dateGroupForm, instructions[1], instructions[2]);
        Integer lesson = Integer.parseInt(instructions[3]);
        try {
            if (visitLog.containsKey(date)) {
                Log log = visitLog.get(date);
                log.addId(lesson, tempUser.ID);
            } else {
                Log log = new Log();
                log.makeNewLog(lesson, tempUser.ID);
                visitLog.put(date, log);
            }
            if (tempUser.name == Long.toString(tempUser.ID)) {
                return strConst.enterName;
            }
            return String.format(strConst.presentForm, date, instructions[3]);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return strConst.wrongInForm;
        }
    }

    private String getTimeTable(String[] instructions, User tempUser) {
        String group = tempUser.group;
        String day = tempUser.day;
        if (isGroup(instructions[0])) {
            group = instructions[0];
            if (instructions.length == 2 && isDay(instructions[1])) {
                day = instructions[1];
            } else if (day == null) {
                return strConst.enterDay;
            }
        } else if (isDay(instructions[0])) {
            day = instructions[0];
            if (instructions.length == 2 && isGroup(instructions[1])) {
                group = instructions[1];
            } else if (group == null) {
                return strConst.enterGroup;
            }
        }
        tempUser.changeGroup(group);
        tempUser.changeDay(day);
        try {
            String text = timeTable.get(group).groupDict.get(day);
            return String.format(strConst.timeTableForm, group, day, text);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private Boolean isDate(String s) {
        try {
            strConst.dateFormat.parse(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isDay(String s) {
        return strConst.WEEK_DAYS.contains(s);
    }

    private boolean isGroup(String s) {
        return (Pattern.matches("^\\D+-\\d+$", s));
    }

    private boolean isLesson(String s) {
        return (Pattern.matches("^\\d ?$", s));
    }

    public String getBotUsername() {
        return "ChatBotTimeTable";
    }

    public String getBotToken() {
        return "0";
    }
}
