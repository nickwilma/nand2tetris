import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Integer> symbols;
    int counter = 16;

    public SymbolTable() {
        symbols = new HashMap<>();
        preDefine();
    }

    private void preDefine() {
        for(int i = 0; i < 16; i++) {
            preDefine("R" + i, i);
        }

        preDefine("SCREEN", 16384);
        preDefine("KDB", 24576);
        preDefine("SP", 0);
        preDefine("LCL", 1);
        preDefine("ARG", 2);
        preDefine("THIS", 3);
        preDefine("THAT", 4);
    }

    public void add(String str) {
        if(!contains(str)) {
            symbols.put(str, counter++);
        }
    }

    public void add(String str, int value) {
        if(!contains(str)) {
            symbols.put(str, value);
        }
    }

    public boolean contains(String str) {
        return symbols.containsKey(str);
    }

    private void preDefine(String str, int value) {
        add(str, value);
    }

    public int get(String str) {
        return symbols.get(str);
    }

    public void print() {
        System.out.println(symbols);
    }
}
