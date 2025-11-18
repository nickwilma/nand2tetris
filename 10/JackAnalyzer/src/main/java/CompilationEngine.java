import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompilationEngine {

    List<IToken> tokens = new ArrayList<>();
    List<Token> supplier = new ArrayList<>();
    Token token = null;

    static HashSet varDecTable, subroutineDecTable, statementsTable, operationTable, termEndTable;

    static {
        varDecTable = new HashSet(Set.of("static", "field", "var"));
        subroutineDecTable = new HashSet(Set.of("constructor", "function", "method"));
        statementsTable = new HashSet(Set.of("let", "do", "if", "while", "return"));
        operationTable = new HashSet(Set.of("+", "-", "*", "/", "<", ">", "=", "&", "|", "~"));
        termEndTable = new HashSet(Set.of(";", ",", ")", "]"));
    }
    public List<XMLFile> run(List<TXMLFile> txmlFiles) {
        List<XMLFile> xmlFiles = new ArrayList<>();
        txmlFiles.forEach(file -> xmlFiles.add(compileTXML(file)));
        return xmlFiles;
    }

    private XMLFile compileTXML(TXMLFile txmlFile) {
        tokens = new ArrayList<>();
        supplier = new ArrayList<>(txmlFile.getTokens());
        while(hasMoreTokens()) {
            advance();
            if(!token.getValue().equals("class")) {
                break;
            }
            handleClass();
        }
        return new XMLFile(txmlFile.getName(), tokens);
    }

    private void handleClass() {
        addTag("class");

        // class
        addToken();

        // name
        advance();
        addToken();

        // open brace
        advance();
        addToken();

        // class var dec
        advance();
        while(isVarDec()) {
            handleClassVarDec();
            advance();
        }

        // subroutine dec
        while(isSubroutineDec()) {
            handleSubroutineDec();
            advance();
        }

        addToken();
        closeTag("class");
    }

    private void handleSubroutineDec() {
        addTag("subroutineDec");

        // write constructor, function, method
        addToken();

        // return type
        advance();
        addToken();

        // name
        advance();
        addToken();

        // parameterlist
        advance();
        addToken();
        advance();

        handleParameterList();
        addToken();

        // subroutine body
        advance();
        handleSubroutineBody();

        closeTag("subroutineDec");
    }

    private void handleSubroutineBody() {
        addTag("subroutineBody");

        // open brace
        addToken();

        // var dec
        advance();
        while(isVarDec()) {
            handleVarDec();
            advance();
        }

        // statements
        if(isStatements()) {
            handleStatements();
        }

        // close brace
        addToken();

        closeTag("subroutineBody");
    }

    private void handleStatements() {
        addTag("statements");

        while(isStatements()) {
            if(token.getValue().equals("let")) {
                handleLetStatement();
                advance();
            } else if(token.getValue().equals("do")) {
                handleDoStatement();
                advance();
            } else if(token.getValue().equals("if")) {
                handeIfStatement();
            } else if(token.getValue().equals("while")) {
                handeWhileStatement();
                advance();
            } else if(token.getValue().equals("return")) {
                handleReturnStatement();
                advance();
            }
        }

        closeTag("statements");
    }

    private void handeWhileStatement() {
        addTag("whileStatement");

        // while
        addToken();

        // open bracket
        advance();
        addToken();

        // expression
        advance();
        handleExpression();

        // close bracket
        addToken();

        // open brace
        advance();
        addToken();

        // statements
        advance();
        if(isStatements()) {
            handleStatements();
        }

        // close brace
        addToken();

        closeTag("whileStatement");
    }

    private void handeIfStatement() {
        addTag("ifStatement");

        // if
        addToken();

        // open bracket
        advance();
        addToken();

        // expression
        advance();
        handleExpression();

        // close bracket
        addToken();

        // open brace
        advance();
        addToken();

        // statements
        advance();
        if(isStatements()) {
            handleStatements();
        }

        // close brace
        addToken();

        // else branch
        advance();
        if(isElse()) {
            // else
            addToken();

            // open brace
            advance();
            addToken();

            // statements
            advance();
            handleStatements();

            // close brace
            addToken();
            advance();
        }

        closeTag("ifStatement");
    }

    private void handleLetStatement() {
        addTag("letStatement");

        // let
        addToken();

        // name
        advance();
        addToken();

        // array
        advance();
        if(isArray()) {
            // open square bracket
            addToken();

            // expression
            advance();
            handleExpression();

            // close square bracket
            addToken();

            advance();
        }

        // =
        addToken();

        // expression
        advance();
        handleExpression();

        // semicolon
        addToken();

        closeTag("letStatement");
    }

    private void handleDoStatement() {
        addTag("doStatement");

        // do
        addToken();

        // name
        advance();
        addToken();

        // symbol
        advance();
        addToken();

        //
        if(isPoint()) {
            // name
            advance();
            addToken();

            // parameter
            advance();
            addToken();
            handleExpressionList();
        } else {
            // parameter
            handleExpressionList();
        }

        // close bracket
        addToken();

        // semicolon
        advance();
        addToken();

        closeTag("doStatement");
    }

    private void handleReturnStatement() {
        addTag("returnStatement");

        // return
        addToken();

        // return value
        advance();
        if(!isSemicolon()) {
            handleExpression();
        }

        // semicolon
        addToken();

        closeTag("returnStatement");
    }

    private void handleExpression() {
        addTag("expression");

        // term
        handleTerm();
        while(!isTermEnd()) {
            addToken();
            advance();
            handleTerm();
        }

        closeTag("expression");
    }

    private void handleTerm() {
        addTag("term");

        if(token.getType() == TokenType.KEYWORD) {
            addToken();
            advance();
        } else if(token.getType() == TokenType.SYMBOL) {
            addToken();
            if(isOperation()) {
                advance();
                handleTerm();
            } else {
                advance();
                handleExpression();
                addToken();
                advance();
            }
        } else if(token.getType() == TokenType.IDENTIFIER) {
            addToken();
            advance();

            if(isArray()) {
                addToken();

                // expression
                advance();
                handleExpression();

                // close square bracket
                addToken();
                advance();
            } else if(isPoint()) {
                addToken();

                // name
                advance();
                addToken();

                // open bracket
                advance();
                addToken();

                // expression list
                handleExpressionList();

                // close bracket
                addToken();
                advance();
            } else if(isBracketOpen()) {
                addToken();

                handleExpressionList();
            }
        } else if(token.getType() == TokenType.INTEGER_CONSTANT) {
            addToken();
            advance();
        } else if(token.getType() == TokenType.STRING_CONSTANT) {
            addToken();
            advance();
        }

        closeTag("term");
    }

    private void handleExpressionList() {
        addTag("expressionList");
        advance();
        while (!isBracketClosed()) {
            if(isComma()) {
                addToken();
                advance();
            }

            // expression
            handleExpression();
        }
        closeTag("expressionList");
    }

    private void handleParameterList() {
        addTag("parameterList");
        while (!isBracketClosed()) {
            // type
            addToken();

            // name
            advance();
            addToken();

            // comma
            advance();
            if(isComma()) {
                addToken();
                advance();
            }
        }
        closeTag("parameterList");
    }

    private void handleClassVarDec() {
        addTag("classVarDec");
        handleVar();
        closeTag("classVarDec");
    }

    private void handleVarDec() {
        addTag("varDec");
        handleVar();
        closeTag("varDec");
    }

    private void handleVar() {
        // static / field
        addToken();

        // type
        advance();
        addToken();

        // name
        advance();
        addToken();

        // list of names
        advance();
        while(isComma()) {
            addToken();
            advance();
            addToken();
            advance();
        }

        // semicolon
        addToken();
    }

    private boolean isBracketClosed() {
        return token.getType() == TokenType.SYMBOL && token.getValue().equals(")");
    }
    private boolean isBracketOpen() {
        return token.getType() == TokenType.SYMBOL && token.getValue().equals("(");
    }
    private boolean isArray() {
        return token.getType() == TokenType.SYMBOL && token.getValue().equals("[");
    }
    private boolean isElse() {
        return token.getType() == TokenType.KEYWORD && token.getValue().equals("else");
    }
    private boolean isTermEnd() {
        return token.getType() == TokenType.SYMBOL && termEndTable.contains(token.getValue());
    }
    private boolean isOperation() {
        return token.getType() == TokenType.SYMBOL && operationTable.contains(token.getValue());
    }

    private boolean isStatements() {
        return token.getType() == TokenType.KEYWORD && statementsTable.contains(token.getValue());
    }

    private boolean isSubroutineDec() {
        return token.getType() == TokenType.KEYWORD && subroutineDecTable.contains(token.getValue());
    }

    private boolean isComma() {
        return token.getType() == TokenType.SYMBOL && token.getValue().equals(",");
    }

    private boolean isSemicolon() {
        return token.getType() == TokenType.SYMBOL && token.getValue().equals(";");
    }

    private boolean isPoint() {
        return token.getType() == TokenType.SYMBOL && token.getValue().equals(".");
    }

    private boolean isVarDec() {
        return token.getType() == TokenType.KEYWORD && varDecTable.contains(token.getValue());
    }

    private void addTag(String tag) {
        tokens.add(new TagToken(tag));
    }

    private void closeTag(String tag) {
        tokens.add(new TagToken("/" + tag));
    }

    private void addToken() {
        tokens.add(token);
    }

    private boolean hasMoreTokens() {
        return !supplier.isEmpty();
    }

    private void advance(){
        token = supplier.get(0);
        supplier.remove(0);
    }
}