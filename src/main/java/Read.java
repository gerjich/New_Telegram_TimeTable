import java.io.File;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;


public class Read {

    public Map<String, String> readOneFile (File fileName) {
        Map<String, String> dict = new HashMap<String, String>();
        try (FileReader fr = new FileReader(fileName);
             Scanner scan = new Scanner(fr))
        {
            while (scan.hasNextLine()) {
                dict.put(scan.nextLine(),scan.nextLine());
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
                    bigDict.put(files[k].getName().substring(0, files[k].getName().length()-4), readOneFile(files[k]));
            }
        }

        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return bigDict;
    }

}

