import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.*;


public class Reader {

    private Map<String, String> readOneFile (File fileName) {
        Map<String, String> dict = new HashMap<String, String>();
        try (FileReader fr = new FileReader(fileName);
             Scanner scan = new Scanner(fr))
        {
            String textKey = "";
            String textValue ="";
            while (scan.hasNextLine()) {
                String text = scan.nextLine();
                if (text.startsWith("/")) {
                    dict.put(textKey, textValue);
                    textKey = text.substring(1);
                    textValue = "";
                }
                else {
                    textValue += "\n" + text;
                }
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return dict;
    }

    public Map<String, Map<String, String>> read() {
        Map<String, Map<String, String>> bigDict = new HashMap<String, Map<String, String>>();
        File dir = new File("FileNames");
        File[] files = dir.listFiles();
        try {
            for (int k = 0; k < files.length; k++) {
                if (files[k].isFile())
                    bigDict.put(files[k].getName().substring(0, files[k].getName().length()-4),
                            readOneFile(files[k]));
            }
        }

        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return bigDict;
    }

}

