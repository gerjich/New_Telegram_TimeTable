import java.util.Set;

public class User  {
    public static Long ID = null;
    public static String group = null;
    public static String day = null;
    public static String name = null;
    public static Boolean teacher = null;

    public void setID(Long id){
        ID = id;
    }

    public void changeGroup(String group){
        this.group = group;
    }

    public void changeDay(String day){
        this.day = day;
    }

    public void changeName(String name){ this.name = name; }

    public Boolean getRights(String password){
        if (password.equals("12345")){
            this.teacher = true;
            return true;
        }
        else {
            this.teacher = false;
            return false;
        }
    }
}
