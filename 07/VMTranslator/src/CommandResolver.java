import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandResolver {
    private final Map<String, String> segmentTable = new HashMap<>();
    private final List<String> hackCommands;
    private int labelCounter = 0;

    public CommandResolver(List<String> hackCommands) {
        this.hackCommands = hackCommands;
        segmentTable.put("static", "16");
        segmentTable.put("local", "LCL");
        segmentTable.put("argument", "ARG");
        segmentTable.put("this", "THIS");
        segmentTable.put("that", "THAT");
        segmentTable.put("temp", "R5");
        segmentTable.put("pointer", "3");
    }

    public void Push(String segment, String value) {
        //static, this, local, argument, that, constant, pointer, or temp
        if(segment.equals("constant")) {
            addMore("@" + value, "D=A");
        } else {
            var deref = segmentTable.get(segment);
            var aOrM = segment.equals("static") || segment.equals("temp") || segment.equals("pointer") ? "A" : "M";
            addMore("@" + deref, "D=" + aOrM, "@" + value, "A=D+A", "D=M");
        }

        addMore("@SP", "A=M", "M=D");
        increaseStackPointer();
    }

    public void Pop(String segment, String value) {
        var deref = segmentTable.get(segment);
        var aOrM = segment.equals("static") || segment.equals("temp") || segment.equals("pointer") ? "A" : "M";
        addMore("@" + deref, "D=" + aOrM, "@" + value, "D=D+A", "@R13", "M=D");
        decreaseStackPointer();
        addMore("D=M", "@R13", "A=M", "M=D");
    }

    public void Add() {
        decreaseStackPointer();
        addMore("D=M", "A=A-1", "M=M+D");
    }

    public void Sub() {
        decreaseStackPointer();
        addMore("D=M", "A=A-1", "M=M-D");
    }

    public void Neg() {
        addMore("@SP", "A=M-1", "M=-M");
    }

    public void Eq() {
        compareByJump("JEQ");
    }

    public void Gt() {
        compareByJump("JGT");
    }

    public void Lt() {
        compareByJump("JLT");
    }

    public void And() {
        bitwiseComparison("&");
    }
    public void Or() {
        bitwiseComparison("|");
    }

    public void Not() {
        addMore("@SP", "A=M-1", "M=!M");
    }

    private void increaseStackPointer() {
        addMore( "@SP", "AM=M+1");
    }

    private void decreaseStackPointer() {
        addMore("@SP", "AM=M-1");
    }

    private void compareByJump(String jumpType) {
        var labelTrue = getUniqueLabel();
        var labelEnd = getUniqueLabel();
        decreaseStackPointer();
        addMore(
            "D=M",
            "A=A-1",
            "D=M-D",
            "@" + labelTrue,
            "D;" + jumpType,
            "@SP",
            "A=M-1",
            "M=0",
            "@" + labelEnd,"0;JMP",
            "(" + labelTrue + ")",
            "@SP",
            "A=M-1",
            "M=-1",
            "(" + labelEnd + ")");
    }

    private void bitwiseComparison(String comparisonType) {
        decreaseStackPointer();
        addMore("D=M", "A=A-1", "M=M" + comparisonType + "D");
    }

    private void add(String command) {
        hackCommands.add(command);
    }
    private void addMore(String ... commands) {
        hackCommands.addAll(Arrays.asList(commands));
    }

    private String getUniqueLabel() {
        return "LABEL_" + labelCounter++;
    }
}


/*      if(segment.equals("static")) {

        } else if(segment.equals("this")) {

        } else if(segment.equals("local")) {

        } else if(segment.equals("argument")) {

        } else if(segment.equals("that")) {

        } else if(segment.equals("constant")) {

        } else if(segment.equals("pointer")) {

        } else if(segment.equals("temp")) {

        }*/
