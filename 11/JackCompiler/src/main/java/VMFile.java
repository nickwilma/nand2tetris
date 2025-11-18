import java.util.List;

public class VMFile {
    private final String name;
    private final List<IToken> tokens;
    public VMFile(String name, List<IToken> tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public String getName() {
        return this.name;
    }

    public List<IToken> getTokens() {
        return this.tokens;
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
