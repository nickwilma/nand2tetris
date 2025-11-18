import java.util.List;

public class JackFile {
    private final String name;
    private final List<String> rows;

    public JackFile(String name, List<String> rows) {
        this.name = name;
        this.rows = rows;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getRows() {
        return this.rows;
    }

    @Override
    public String toString() {
        return name + rows;
    }
}
