import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHandler {

    public List<String> read(String path) {
        File folder = new File(path);
        return read(folder);
    }
    private List<String> read(File folder) {
        List<String> data = new ArrayList<>();
        if(!folder.isDirectory()) {
            data.addAll(getFileContent(folder));
            return data;
        }

        for(File file : folder.listFiles()) {
            if (file.isDirectory()) {
                data.addAll(read(file));
            } else {
                var name = file.getName();
                System.out.println("File Handler::scanning " + name);
                if(name.endsWith(".vm")) {
                    System.out.println("File Handler::collecting " + name);
                    data.addAll(getFileContent(file));
                }
            }
        }
        return data;
    }

    private List<String> getFileContent(File file) {
        List<String> content = new ArrayList<>();
        try {
            Scanner reader = new Scanner(file);
            content.add("NEW_FILE " + file.getName().split("\\.")[0]);
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

    public void write(String location, List<String> content) {
        location += "/" + new File(location).getName() + ".asm";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));
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
