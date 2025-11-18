

public class App {
    public static void main(String[] args) {
        Assembler assembler = new Assembler(args[0], args.length > 1 ? args[1] : "output.hack");
    }
}
