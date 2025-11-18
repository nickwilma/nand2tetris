

public class VMToken implements IToken {
    private final String name;

    public VMToken(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
