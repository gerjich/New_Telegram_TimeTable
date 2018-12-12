public class GroupSchedule {
    public static String GrouoSchedule(String[] arg){
        String group = arg[1].toLowerCase();
        String day = arg[2].toLowerCase();
        //String parity = arg[3].toLowerCase();
        String path = "\\Telegram_TimeTable-master\\Telegram_TimeTable-master\\";
        String fileName = group.substring(1) + day;

        return FileReader.FileReader(path + fileName);
        /*
        if(group.equals("KN201") && day.equals("MONDAY") && parity.equals("EVEN")){
            return FileReader.FileReader(path + "KN201MONDAYEVEN");
        }
        if(group.equals("KN201") && day.equals("MONDAY") && parity.equals("ODD")){
            return FileReader.FileReader(path + "KN201MONDAYODD");
        }
        if(group.equals("KN202") && day.equals("MONDAY") && parity.equals("EVEN")){
            return FileReader.FileReader(path + "KN202MONDAYEVEN");
        }
        if(group.equals("KN202") && day.equals("MONDAY") && parity.equals("ODD")){
            return FileReader.FileReader(path + "KN202MONDAYODD");
        }
        return "";
        */
    }
}
