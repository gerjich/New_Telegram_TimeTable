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
            String[] Arr = new String[14];
            int c = 0;
            while (scan.hasNextLine()) {
                String text = scan.nextLine();
                if (text.substring(0,1).equals("/")) {
                    Arr[c] = text.substring(1);
                    c++;
                }
                else {
                    Arr[c - 1] += "\n" + text;
                }
            }
            for (int i = 0; i<Arr.length/2; i++){
                dict.put(Arr[i*2], Arr[i*2+1]);
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

