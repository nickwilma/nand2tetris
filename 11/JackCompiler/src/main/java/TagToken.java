

public class TagToken implements IToken {
    private final String name;

    public TagToken(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}
