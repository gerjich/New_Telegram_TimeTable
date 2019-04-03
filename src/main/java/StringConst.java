import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringConst {
    public static final String help = "help";
    public static final String commands = "commands";
    public static final String name = "name";
    public static final String password = "password";
    public static final String correctPassword = "correct password";
    public static final String wrongPassword = "wrong password";
    public static final String show = "show";
    public static final String present = "present";
    public static final String wrongIn = "Wrong input";
    public static final String enterPass = "Enter the password";
    public static final String wrongInForm = "wrong input format";
    public static final String enterName = "Enter your name";
    public static final String enterDay = "Enter the day of week";
    public static final String enterGroup = "Enter the group";


    public static final Set<String> WEEK_DAYS = new HashSet<>(
            Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "sunday"));

    public String getNewName(String newName){
        return String.format("Your new name is %s", newName);
    }
    public String getDate(String date, String group){
        return String.format("%1$s %2$s", date, group);
    }
    public String noLesson(String date){
        return String.format("there were no lessons in %s", date);
    }
    public String getPresentStr(String date, String lesson){
        return String.format("You were %1$s %2$s", date, lesson);
    }
    public String getTableForm(String group, String day, String text){
        return String.format("%1$s %2$s\n\n%3$s", group, day, text);
    }
    public String getAddedLine(String text1, String text2) {
        return String.format("%1$s\n%2$s", text1, text2);
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

    public void makeConst() {
        dateFormat.setLenient(false);
    }
}
