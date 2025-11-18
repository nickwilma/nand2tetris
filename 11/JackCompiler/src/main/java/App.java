

public class App {
    public static void main(String[] args) {
        var fileHandler = new FileHandler();
        var tokenizer = new Tokenizer();
        var vmcompliationEngine = new VMCompilationEngine();

        var jackFiles = fileHandler.read(args[0]);

        var txmlFiles = tokenizer.run(jackFiles);

        var vmFiles = vmcompliationEngine.run(txmlFiles);
        vmFiles.forEach(file -> fileHandler.write2(args[0] + file.getName().split("\\.")[0] + ".vm", file.getTokens()));
    }
}
