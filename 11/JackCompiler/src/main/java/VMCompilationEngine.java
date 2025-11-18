import java.util.*;

public class VMCompilationEngine {

    List<IToken> tokens = new ArrayList<>();
    List<Token> supplier = new ArrayList<>();
    Map<String, Pair<Integer, String, String>> classVars = new HashMap<>();
    Map<String, Pair<Integer, String, String>> subroutineVars = new HashMap<>();

    String className;
    Token token = null;
    int fieldCounter = 0;
    int staticCounter = 0;
    int argumentCounter = 0;
    int varCounter = 0;
    int ifLabelCounter = 0;
    int whileLabelCounter = 0;

    static HashSet varDecTable, subroutineDecTable, statementsTable, operationTable, termEndTable;

    static {
        varDecTable = new HashSet(Set.of("static", "field", "var"));
        subroutineDecTable = new HashSet(Set.of("constructor", "function", "method"));
        statementsTable = new HashSet(Set.of("let", "do", "if", "while", "return"));
        operationTable = new HashSet(Set.of("+", "-", "*", "/", "<", ">", "=", "&", "|", "~"));
        termEndTable = new HashSet(Set.of(";", ",", ")", "]"));
    }

    private void clearClassVars() {
        fieldCounter = 0;
        staticCounter = 0;
        classVars = new HashMap<>();
    }

    private void clearSubroutineVars() {
        ifLabelCounter = 0;
        whileLabelCounter = 0;
        argumentCounter = 0;
        varCounter = 0;
        subroutineVars = new HashMap<>();
    }
    public List<VMFile> run(List<TXMLFile> txmlFiles) {
        List<VMFile> xmlFiles = new ArrayList<>();
        txmlFiles.forEach(file -> xmlFiles.add(compileTXML(file)));
        return xmlFiles;
    }

    private VMFile compileTXML(TXMLFile txmlFile) {
        tokens = new ArrayList<>();
        supplier = new ArrayList<>(txmlFile.getTokens());
        className = "";
        while(hasMoreTokens()) {
            advance();
            if(!token.getValue().equals("class")) {
                break;
            }
            handleClass();
        }
        System.out.println(classVars);
        System.out.println(subroutineVars);
        return new VMFile(txmlFile.getName(), tokens);
    }

    private void handleClass() {
        clearClassVars();
        addTag("class");

        // class
        addToken();

        // name
        advance();
        addToken();

        className = token.getValue();

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
        clearClassVars();
    }

    private void handleSubroutineDec() {
        clearSubroutineVars();
        addTag("subroutineDec");

        // write constructor, function, method
        addToken();

        String type = token.getValue();

        // return type
        advance();
        addToken();

        // name
        advance();
        addToken();

        String name = token.getValue();

        // parameterlist
        advance();
        addToken();
        advance();

        // experimental
        // maybe with type.equals("constructor") also ???
        if(type.equals("method")) {
            argumentCounter++;
        }

        handleParameterList();
        addToken();

        // subroutine body
        advance();
        handleSubroutineBody(type, name);

        closeTag("subroutineDec");
    }

    private void handleSubroutineBody(String type, String name) {
        addTag("subroutineBody");

        // open brace
        addToken();

        // var dec
        advance();
        while(isVarDec()) {
            handleVarDec();
            advance();
        }


        if(type.equals("function")) {
            addFunction(name);
        } else if(type.equals("constructor")) {
            addConstructor(name);
        } else if(type.equals("method")) {
            addMethod(name);
        }

        //addVM("subroutineVars: " + subroutineVars);

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
        int label = whileLabelCounter++;

        addTag("whileStatement");

        // while
        addToken();

        addLabel("WHILE_EXP" + label);

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

        addNot();
        addIfGoto("WHILE_END" + label);

        // statements
        advance();
        if(isStatements()) {
            handleStatements();
        }

        //addVM("123");
        addGoto("WHILE_EXP" + label);
        addLabel("WHILE_END" + label);

        // close brace
        addToken();

        closeTag("whileStatement");
    }

    private void handeIfStatement() {
        addTag("ifStatement");

        int label = getLabel();

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

        addIfGoto("IF_TRUE" + label);
        addGoto("IF_FALSE" + label);
        addLabel("IF_TRUE" + label);
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
            addGoto("IF_END" + label);
            addLabel("IF_FALSE" + label);
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
            addLabel("IF_END" + label);
        } else {
            addLabel("IF_FALSE" + label);
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

        String name = token.getValue();
        boolean hasArray = false;

        // array
        advance();
        if(isArray()) {
            // open square bracket
            addToken();

            // expression
            advance();
            handleExpression();

            // maybe must be one line higher???
            Pair<Integer, String, String> pair = getSegmentInFunction(name);
            if(pair != null) {
                addPush(pair.getSecond(), pair.getFirst());
            }

            // close square bracket
            addToken();

            advance();

            addAdd();

            hasArray = true;
        }

        // =
        addToken();

        // expression
        advance();
        handleExpression();

        if(!hasArray) {
            Pair<Integer, String, String> pair = getSegmentInFunction(name);
            if(pair != null) {
                addPop(pair.getSecond(), pair.getFirst());
            }
        } else {
            addPop("temp", 0);
            addPop("pointer", 1);
            addPush("temp", 0);
            addPop("that", 0);
        }


        // semicolon
        addToken();

        closeTag("letStatement");
    }

    private void handleDoStatement() {
        int args;
        addTag("doStatement");

        // do
        addToken();

        // name
        advance();
        addToken();

        String scopedName = token.getValue();

        // symbol
        advance();
        addToken();

        //
        if(isPoint()) {
            // name
            advance();
            addToken();

            //addVM("here2 ???");

            String name = token.getValue();

            //addVM("here2 ???" + name);
            Pair<Integer, String, String> pair = getSegmentInFunction(name);
            if(pair != null) {
                addPush(pair.getSecond(), pair.getFirst());
            } else {
                //addVM("not found :(");
                // maybe push local here
                //addPush("local", 0);

            }
            //addVM("!");
            // parameter
            /*advance();
            addToken();
            args = handleExpressionList();*/

            //addVM(scopedName + (getSegmentInFunction(scopedName) == null));

            if(getSegmentInFunction(scopedName) == null) {
                advance();
                addToken();
                args = handleExpressionList();
                addCall(scopedName + "." + name, args);
            } else {
                Pair<Integer, String, String> pair1 = getSegmentInFunction(scopedName);
                //addVM("?");
                addPush(pair1.getSecond(), pair1.getFirst());
                advance();
                addToken();
                args = handleExpressionList();
                addCall(pair1.getThird() + "." + name, args + 1);
            }


        } else {
            addPush("pointer", 0);

            // parameter
            args = handleExpressionList();

            addCall(className + "." + scopedName, args + 1);
        }

        // close bracket
        addToken();

        // semicolon
        advance();
        addToken();

        addPop("temp", 0);

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
        } else {
            addPush("constant", 0);
        }

        addReturn();

        // semicolon
        addToken();

        closeTag("returnStatement");
    }

    private void handleExpression() {
        addTag("expression");

        // term
        handleTerm();
        while(!isTermEnd()) {
            String term = token.getValue();
            addToken();
            advance();
            handleTerm();
            handleUnary(term);
        }

        closeTag("expression");
    }

    private void handleTerm() {
        addTag("term");
        //addVM(token.getValue() + "::" + token.getType().toString());
        if(token.getType() == TokenType.KEYWORD) {
            //addVM("0");
            addToken();
            String value = token.getValue();
            if(value.equals("false") || value.equals("null")) {
                addPush("constant", 0);
            } else if(value.equals("true")) {
                addPush("constant", 0);
                addNot();
            } else if(value.equals("this")) {
                addPush("pointer", 0);
            }
            advance();
        } else if(token.getType() == TokenType.SYMBOL) {
            //addVM("1");
            addToken();
            if(token.getValue().equals("-")) {
                advance();
                handleTerm();
                addNeg();
            } else if(token.getValue().equals("~")) {
                advance();
                handleTerm();
                addNot();
            } /*else if(isOperation()) {
                advance();
                String term = token.getValue();
                handleTerm();
                //System.out.println("NICK: " + term);
                //handleUnary(term); // maybe wrong???

                //addVM("?????");

            }*/ else {
                advance();
                handleExpression();
                addToken();
                advance();
            }
        } else if(token.getType() == TokenType.IDENTIFIER) {
            //addVM("2");
            String scopedName = token.getValue();

            addToken();
            advance();

            //addVM("NICK: " + token.getValue());

            if(isArray()) {
                addToken();

                // expression
                advance();
                handleExpression();

                Pair<Integer, String, String> pair = getSegmentInFunction(scopedName);
                if(pair != null) {
                    addPush(pair.getSecond(), pair.getFirst());
                }

                addAdd();
                addPop("pointer", 1);
                addPush("that", 0);

                // close square bracket
                addToken();
                advance();
            } else if(isPoint()) {
                addToken();

                // name
                advance();
                addToken();

                //addVM("here???");

                String name = token.getValue();
                Pair<Integer, String, String> pair = getSegmentInFunction(scopedName);
                if(pair != null) {
                    addPush(pair.getSecond(), pair.getFirst());
                } else {
                    //addVM("not found :(");
                }
                //System.out.println("NICK: " + scopedName);

                // open bracket
                advance();
                addToken();

                // expression list
                int args = handleExpressionList();

                // close bracket
                addToken();
                advance();

                if(getSegmentInFunction(scopedName) == null) {
                    addCall(scopedName + "." + name, args);
                } else {
                    Pair<Integer, String, String> pair1 = getSegmentInFunction(scopedName);
                    //addVM("?");
                    addCall(pair1.getThird() + "." + name, args + 1);
                }

                //addCall(scopedName + "." + name, args);
            } else if(isBracketOpen()) {

                addToken();

                addPush("pointer", 0);

                int args = handleExpressionList();

                addCall(className + "." + scopedName, args);
            } else {
                Pair<Integer, String, String> pair = getSegmentInFunction(scopedName);
                if(pair != null) {
                    addPush(pair.getSecond(), pair.getFirst());
                } else {
                    addVM("----------------------------- " + scopedName + " " + subroutineVars);
                }
            }
        } else if(token.getType() == TokenType.INTEGER_CONSTANT) {
            //addVM("3");
            addPush("constant", Integer.parseInt(token.getValue()));
            addToken();
            advance();
        } else if(token.getType() == TokenType.STRING_CONSTANT) {
            //addVM("4");
            String str = token.getValue();
            addPush("constant", str.length());
            addCall("String.new", 1);
            for(char c : str.toCharArray()) {
                addPush("constant", c);
                addCall("String.appendChar", 2);
            }

            addToken();
            advance();
        }

        closeTag("term");
    }

    private int handleExpressionList() {
        int args = 0;
        addTag("expressionList");
        advance();
        while (!isBracketClosed()) {
            if(isComma()) {
                addToken();
                advance();
            }

            // expression
            handleExpression();
            args++;
        }
        closeTag("expressionList");
        return args;
    }

    private void handleParameterList() {
        addTag("parameterList");
        while (!isBracketClosed()) {
            // type
            addToken();

            // name
            advance();
            addToken();

            subroutineVars.put(token.getValue(), new Pair<>(argumentCounter++, "argument", ""));

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
        String type = token.getValue();

        // type
        advance();
        addToken();
        String type2 = token.getValue();

        // name
        advance();
        addToken();

        String name = token.getValue();
        if(type.equals("static")) {
            classVars.put(name, new Pair<>(staticCounter++, "static", type2));
        } else if(type.equals("field")) {
            classVars.put(name, new Pair<>(fieldCounter++, "this", type2));
        } else if(type.equals("var")) {
            subroutineVars.put(name, new Pair<>(varCounter++, "local", type2));
        }

        // list of names
        advance();
        while(isComma()) {
            addToken();
            advance();
            String name1 = token.getValue();

            if(type.equals("static")) {
                classVars.put(name1, new Pair<>(staticCounter++, "static", type2));
            } else if(type.equals("field")) {
                classVars.put(name1, new Pair<>(fieldCounter++, "this", type2));
            } else if(type.equals("var")) {
                subroutineVars.put(name1, new Pair<>(varCounter++, "local", type2));
            }

            addToken();
            advance();
        }

        // semicolon
        addToken();
    }

    private void handleUnary(String unary) {
        if(unary.equals("+")) {
            addAdd();
        } else if(unary.equals("-")) {
            addSub();
        } else if(unary.equals("*")) {
            addMultiply();
        } else if(unary.equals("/")) {
            addDivide();
        } else if(unary.equals("~")) {
            addNot();
        } else if(unary.equals("&lt;")) {
            addLessThan();
        } else if(unary.equals("&gt;")) {
            addGreaterThan();
        } else if(unary.equals("=")) {
            addEquals();
        } else if(unary.equals("&amp;")) {
            addAnd();
        } else if(unary.equals("|")) {
            addOr();
        } else {
            addVM("no unary found: " + unary);
        }
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

    private void addVM(String vm) {
        tokens.add(new VMToken(vm));
    }

    private void addTag(String tag) {
        //tokens.add(new TagToken(tag));
    }

    private void closeTag(String tag) {
        //tokens.add(new TagToken("/" + tag));
    }

    private void addToken() {
        //tokens.add(token);
    }

    private boolean hasMoreTokens() {
        return !supplier.isEmpty();
    }

    private void advance(){
        if(!hasMoreTokens()) {
            throw new RuntimeException();
        }
        token = supplier.get(0);
        supplier.remove(0);
    }

    private void addPush(String segment, int value) {
        addVM("push " + segment + " " + value);
    }

    private void addPop(String segment, int value) {
        addVM("pop " + segment + " " + value);
    }

    private void addCall(String name, int value) {
        addVM("call " + name + " " + value);
    }

    private void addConstructor(String name) {
        addFuncOrMethOrCon(name);
        //if(fieldCounter > 0) {
            addPush("constant", fieldCounter);
        //}
        addCall("Memory.alloc", 1);
        addPop("pointer", 0);
    }

    private void addMethod(String name) {
        addFuncOrMethOrCon(name);
        addPush("argument", 0);
        addPop("pointer", 0);
        //argumentCounter++; // maybe wrong??
    }

    private void addFunction(String name) {
        addFuncOrMethOrCon(name);
    }

    private void addFuncOrMethOrCon(String name) {
        addVM("function " + className + "." + name + " " + varCounter);
    }

    private void addReturn() {
        addVM("return");
    }

    private void addAdd() {
        addVM("add");
    }

    private void addSub() {
        addVM("sub");
    }

    private void addMultiply() {
        addCall("Math.multiply", 2);
    }

    private void addDivide() {
        addCall("Math.divide", 2);
    }

    private void addNot() {
        addVM("not");
    }

    private void addNeg() {
        addVM("neg");
    }

    private void addAnd() {
        addVM("and");
    }

    private void addOr() {
        addVM("or");
    }

    private void addLessThan() {
        addVM("lt");
    }

    private void addGreaterThan() {
        addVM("gt");
    }

    private void addEquals() {
        addVM("eq");
    }

    private Pair<Integer, String, String> getSegmentInFunction(String value) {
        if(subroutineVars.containsKey(value)) {
            return subroutineVars.get(value);
        }
        if(classVars.containsKey(value)) {
            return classVars.get(value);
        }
        return null;
    }

    private int getLabel() {
        return ifLabelCounter++;
    }

    private void addIfGoto(String label) {
        addVM("if-goto " + label);
    }

    private void addGoto(String label) {
        addVM("goto " + label);
    }

    private void addLabel(String label) {
        addVM("label " + label);
    }
}