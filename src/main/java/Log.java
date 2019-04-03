import java.lang.reflect.Array;
import java.util.*;

public class Log {
    public Map<Integer, Set<Long>> lessonNumber = new HashMap<>(); // key - number of lesson; value - students id
    public int numberOfStudents;

    public void addId(Integer lesson, Long newId){
        lessonNumber.get(lesson).add(newId);
        numberOfStudents+=1;
    }

    public void makeNewLog(Integer lesson, Long newId){
        lessonNumber.put(lesson, new HashSet<>(Arrays.asList(newId)));

    }


}
