package marks.bs.datamodels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

/**
 * Created by Богдан on 01.02.2016.
 */
public class Data implements Serializable {
    private HashMap<String, ArrayList<Mark>> data;
    private File file;

    private static Data ourInstance;

    public static Data getInstance(String path) {
        if(ourInstance == null) {
            ourInstance = new Data(path);
        }
        return ourInstance;
    }


    private Data(String path) {
        file = new File(path + "/data_marks");
        if(!file.exists()) {
            data = new HashMap<String, ArrayList<Mark>>();
        } else {
            try {
                readData();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                data = new HashMap<String, ArrayList<Mark>>();
            } catch (IOException e) {
                e.printStackTrace();
                data = new HashMap<String, ArrayList<Mark>>();
            }
        }
    }

    public void readData() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        data = (HashMap) in.readObject();
    }

    public void saveData() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(data);
    }

    public Set<String> getSubjects() {
        return data.keySet();
    }

    public void addSubject(String name) {
        data.put(name, new ArrayList<Mark>());
    }

    public void deleteSubject(String name) {
        data.remove(name);
    }

    public ArrayList<Mark> getMarks(String subjectName) {
        return data.get(subjectName);
    }

    public void addMark(String subjectName, Mark mark) {
        data.get(subjectName).add(mark);
    }

    public void deleteMark(String subjectName, int number) {
        data.get(subjectName).remove(number);
    }

    public void deleteMark(String subjectName, Mark mark) {
        data.get(subjectName).remove(mark);
    }

    public void editSubject(String oldName, String newName) {
        data.put(newName, data.remove(oldName));
    }

    public void editMark(String subjectName, Mark oldMark, Mark newMark) {
        data.get(subjectName).set(data.get(subjectName).indexOf(oldMark), newMark);
    }

    public boolean subjectExist(String subjectName) {
        return data.containsKey(subjectName);
    }

}
