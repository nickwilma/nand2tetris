import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public abstract class Writer {

    public static void writeFileContent(String filename, List<String> content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            content.forEach(item -> {
                try {
                    writer.write(item);
                    writer.newLine();
                } catch (IOException e) {
                    System.out.println("ERROR: File could not be written!");
                }
            });

            writer.close();
        } catch (IOException e) {
            System.out.println("ERROR: File could not be written!");
            e.printStackTrace();
        }
    }
}
