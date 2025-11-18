

public class App {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("ERROR: You need at least two arguments -> input.txt, output.txt");
            return;
        }

        final Translator translator = new Translator();
        final FileHandler fileHandler = new FileHandler();

        var input = fileHandler.read(args[0]);
        var output = translator.translate(input);

        var outputFile = args[0].replace("vm", "asm");
        fileHandler.write(outputFile, output);

        System.out.println("VM Translator: JOB DONE");
    }
}
