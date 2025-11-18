import java.util.List;

public class TXMLFile {
    private final String name;
    private final List<Token> tokens;
    public TXMLFile(String name, List<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public String getName() {
        return this.name;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
