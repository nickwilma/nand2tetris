

public class App {
    public static void main(String[] args) {
        var fileHandler = new FileHandler();
        var tokenizer = new Tokenizer();
        var compliationEngine = new CompilationEngine();

        var jackFiles = fileHandler.read(args[0]);
        System.out.println(jackFiles);

        var txmlFiles = tokenizer.run(jackFiles);
        System.out.println(txmlFiles);

        txmlFiles.forEach(file -> fileHandler.write(args[0] + file.getName().split("\\.")[0] + "T.xml", file.getTokens()));

        var xmlFiles = compliationEngine.run(txmlFiles);
        System.out.println(xmlFiles);
        xmlFiles.forEach(file -> fileHandler.write2(args[0] + file.getName().split("\\.")[0] + ".xml", file.getTokens()));

    }
}
