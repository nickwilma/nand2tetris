import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHandler {

    public List<JackFile> read(String path) {
        File folder = new File(path);
        return read(folder);
    }
    private List<JackFile> read(File folder) {
        List<JackFile> jackFiles = new ArrayList<>();
        if(!folder.isDirectory()) {
            var jackFile = new JackFile(
                folder.getName(),
                getFileContent(folder));
            jackFiles.add(jackFile);
            return jackFiles;
        }

        for(File file : folder.listFiles()) {
            if (file.isDirectory()) {
                jackFiles.addAll(read(file));
            } else {
                var name = file.getName();
                System.out.println("File Handler::scanning " + name);
                if(name.endsWith(".jack")) {
                    System.out.println("File Handler::collecting " + name);
                    var jackFile = new JackFile(
                        file.getName(),
                        getFileContent(file));
                    jackFiles.add(jackFile);
                }
            }
        }
        return jackFiles;
    }

    private List<String> getFileContent(File file) {
        List<String> content = new ArrayList<>();
        try {
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

    public void write(String location, List<Token> tokens) {
        //location += "/tmp_" + new File(location).getName() + "T.xml";
        System.out.println(location + " " + tokens.size());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));
            writer.write("<tokens>");
            writer.newLine();
            for(Token token : tokens) {
                writer.write(token.toString());
                writer.newLine();
            }
            writer.write("</tokens>");
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("ERROR: File could not be written");
            e.printStackTrace();
        }
    }

    public void write2(String location, List<IToken> tokens) {
        //location += "/tmp_" + new File(location).getName() + "T.xml";
        System.out.println(location + " " + tokens.size());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));

            for(IToken token : tokens) {
                writer.write(token.toString());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("ERROR: File could not be written");
            e.printStackTrace();
        }
    }
}
