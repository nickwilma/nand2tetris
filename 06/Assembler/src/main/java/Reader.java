import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class Reader {

    public static String getFileContent(String filename) {
        try {
            File file = new File(filename);
            Scanner reader = new Scanner(file);
            String data = "";
            while (reader.hasNextLine()) {
                data += reader.nextLine() + "\n";
            }
            reader.close();
            return data;
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File could not be read!");
            e.printStackTrace();
        }
        return "";
    }
}