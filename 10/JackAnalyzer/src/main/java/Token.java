
public class Token implements IToken {

    private final String value;
    private final TokenType tokenType;
    public Token(String value) {
        this.value = value.trim();
        this.tokenType = TokenType.getType(value.trim());
    }

    public String getValue() {
        if(tokenType == TokenType.STRING_CONSTANT) {
            if(value.length() == 0) {
                return "";
            }
            String tmp = "";
            for(char c : value.toCharArray()) {
                if(c != '"') {
                    tmp += c + "";
                }
            }
            return tmp;
        }
        if(tokenType == TokenType.SYMBOL) {
            if(value.equals("<")) {
                return "&lt;";
            } else if(value.equals(">")) {
                return "&gt;";
            } else if(value.equals("&")) {
                return "&amp;";
            }
        }
        return value;
    }

    public TokenType getType() {
        return tokenType;
    }

    public boolean isEmpty() {
        return value.isBlank();
    }

    @Override
    public String toString() {
        return "<" + getType() + "> " + getValue() + " </" + getType() + ">";
    }
}
