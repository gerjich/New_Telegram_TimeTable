public class User  {
    public static Long ID = null;
    public static String Group = null;
    public static String Day = null;
    public static String Name = null;
    public static Boolean Teacher = null;

    public static void setID(Long id){
        ID = id;
    }

    public static void changeGroup(String group){
        Group = group;
    }

    public static void changeDay(String day){
        Day = day;
    }

    public static void changeName(String name){
        Name = name;
    }

    public static Boolean getRights(String password){
        if (password.equals("12345")){
            Teacher = true;
            return true;
        }
        else {
            Teacher = false;
            return false;
        }
    }
}
