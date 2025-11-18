import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHandler {

    public List<String> read(String filename) {
        List<String> content = new ArrayList<>();
        try {
            File file = new File(filename);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                content.add(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File could not be read");
            e.printStackTrace();
        }
        return content;
    }

    public void write(String filename, List<String> content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for(String item : content) {
                writer.write(item);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("ERROR: File could not be written");
            e.printStackTrace();
        }
    }
}
