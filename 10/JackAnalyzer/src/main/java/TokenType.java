import java.util.HashSet;
import java.util.List;

public enum TokenType {
    SYMBOL,
    KEYWORD,
    IDENTIFIER,
    STRING_CONSTANT,
    INTEGER_CONSTANT,
    EMPTY;
    static final HashSet<String> symbolSet = new HashSet<>(List.of("{", "}", "(", ")", "[", "]", ".", "=", ";", "*", "/", "-", "+", ",", "|", "&", "~", "<", ">"));
    static final HashSet<String> keywordSet = new HashSet<>(List.of("class", "this", "static", "constructor", "field", "boolean", "int", "char", "function", "method", "void", "var", "let", "do", "return", "if", "false", "true", "null", "else", "while"));

    public static TokenType getType(String value) {
        if(value.isBlank()) {
            return TokenType.EMPTY;
        }
        if(symbolSet.contains(value)) {
            return TokenType.SYMBOL;
        } else if(keywordSet.contains(value)) {
            return TokenType.KEYWORD;
        } else if(value.charAt(0) == '"') {
            return TokenType.STRING_CONSTANT;
        } else if(isInteger(value)) {
            return TokenType.INTEGER_CONSTANT;
        }
        return TokenType.IDENTIFIER;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        if(name().equals("STRING_CONSTANT")) {
            return "stringConstant";
        } else if(name().equals("INTEGER_CONSTANT")) {
            return "integerConstant";
        }
        return name().toLowerCase();
    }
}
