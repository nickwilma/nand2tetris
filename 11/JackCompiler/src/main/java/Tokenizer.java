import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Tokenizer {
    /* NOTE: " is missing */
    private final HashSet<String> splitTokenSet;

    public Tokenizer() {
        splitTokenSet = new HashSet();
        splitTokenSet.addAll(List.of("{", "}", "(", ")", "[", "]", ".", "=", ";", "*", "/", "-", "+", ",", "|", "&", "~", "<", ">"));
    }

    boolean foundOpenComment = false;
    boolean foundOpenString = false;

    public List<TXMLFile> run(List<JackFile> jackFiles) {
        List<TXMLFile> txmlFiles = new ArrayList<>();
        jackFiles.forEach(file -> txmlFiles.add(tokenizeJackFile(file)));
        return txmlFiles;
    }

    private TXMLFile tokenizeJackFile(JackFile jackFile) {
        List<Token> tokens = new ArrayList<>();
        jackFile.getRows().forEach(row -> tokens.addAll(tokenizeRow(row)));
        return new TXMLFile(jackFile.getName(), tokens);
    }

    private List<Token> tokenizeRow(String row) {
        row = filterBasicComment(row);
        List<Token> tokens = new ArrayList<>();
        var separatedRow = row.toCharArray();
        String unpreparedToken = "";
        for (char c : separatedRow) {
            String character = c + "";
            if(c == '"') {
                foundOpenString = !foundOpenString;
            } else if (c == ' ' && !foundOpenString) {
                //System.out.println(unpreparedToken);
                tokens.addAll(getPreparedTokens(unpreparedToken));
                unpreparedToken = "";
            }
            unpreparedToken += character;
        }
        //System.out.println(unpreparedToken);
        tokens.addAll(getPreparedTokens(unpreparedToken));
        tokens.removeIf(Token::isEmpty);
        return tokens;
    }

    private List<Token> getPreparedTokens(String unpreparedToken) {
        if(unpreparedToken.contains("/*")) {
            foundOpenComment = true;
        }
        if(unpreparedToken.contains("*/")) {
            foundOpenComment = false;
            return List.of();
        }
        if(foundOpenComment) {
            return List.of();
        }
        return splitToken(unpreparedToken);
    }

    private List<Token> splitToken(String preparedToken) {
        boolean openString = false;
        List<Token> tokens = new ArrayList<>();
        String value = "";
        for(int i = 0; i < preparedToken.length(); i++) {
            char tokenChar = preparedToken.charAt(i);
            if(tokenChar == '"') {
                openString = !openString;
            }
            String tc = tokenChar + "";
            if(splitTokenSet.contains(tc) && !openString) {
                tokens.add(new Token(value));
                tokens.add(new Token(tc));
                value = "";
                continue;
            }
            value += tc;
        }
        tokens.add(new Token(value));
        return tokens;
    }

    private String filterBasicComment(String row) {
        var splitRow = row.split("//");
        if(splitRow.length > 0) {
            return splitRow[0];
        }
        return "";
    }
}
