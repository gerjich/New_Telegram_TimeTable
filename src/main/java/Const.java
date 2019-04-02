import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Const {
    public static final Set<String> WEEK_DAYS = new HashSet<>();


    public static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");

    public void makeConst() {
        WEEK_DAYS.addAll(Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "sunday"));
        format.setLenient(false);
    }
}
