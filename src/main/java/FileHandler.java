import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 04/06/2016.
 */
public class FileHandler {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public ArrayList<String> readFromFile(String file) {
        try {
            if (!Paths.get(file).toFile().exists()) {
                Files.createFile(Paths.get(file));
            }
            List<String> fileContents;
            fileContents = Files.readAllLines(Paths.get(file));
            ArrayList<String> content = (ArrayList<String>) fileContents;
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeToFile(String file, int line, String text) {
        try {
            ArrayList<String> fileContents = readFromFile(file);
            fileContents.set(line, text);
            Files.write(Paths.get(file), fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String file, String text){
        try {
            if(!Files.exists(Paths.get(file))){
                Files.createFile(Paths.get(file));
            }
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append(text + "\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readfromJson(String file, Class<?> objClass){
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            Object newObject = gson.fromJson(reader, objClass);
            return newObject;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writetoJson(String file, Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}