package data;

//@author sasha
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadData {

    private String fileName;
    private String path;
    private ArrayList data;

    public LoadData(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        this.data = new ArrayList();
        this.data = loadData();
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public ArrayList getData() {
        return data;
    }

    //Load data
    public ArrayList loadData() {

        String line;      
        try {
            //Read file at specified path
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path + fileName));

            while ((line = bufferedReader.readLine()) != null) {
                
                //Create array for each instance of data
                ArrayList<String> instance = new ArrayList<String>();
                int varCount = line.split(" ").length;
                
                for (int i = 0; i < varCount; i++) {
                    instance.add(line.split(" ")[i]);
                }                
                data.add(instance);
            }

            //Close
            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }

        return data;
    }
    
}// End LoadData
