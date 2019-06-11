import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringConst {
    public static final String fileNames = "FileNames";
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

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

    public static final Set<String> WEEK_DAYS = new HashSet<>(
            Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "sunday"));

    public static final String newNameFotm = "Your new name is %s";
    public static final String dateGroupForm = "%1$s %2$s";
    public static final String noLessonForm = "there were no lessons in %s";
    public static final String presentForm = "You were %1$s %2$s";
    public static final String timeTableForm = "%1$s %2$s\n\n%3$s";
    public static final String addedLineForm = "%1$s\n%2$s";
}
