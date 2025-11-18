import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CommandResolver {
    private final Map<String, String> segmentTable;
    private final List<String> hackCommands;
    private int labelCounter = 0;
    private String currentFileName = "";

    public CommandResolver(List<String> hackCommands) {
        this.segmentTable = new HashMap<>();
        this.hackCommands = hackCommands;
    }

    public void init() {
        segmentTable.put("static", "16");
        segmentTable.put("local", "LCL");
        segmentTable.put("argument", "ARG");
        segmentTable.put("this", "THIS");
        segmentTable.put("that", "THAT");
        segmentTable.put("temp", "R5");
        segmentTable.put("pointer", "3");

        initStackPointer();
        Call("Sys.init", "0");
    }

    private void initStackPointer() {
        addMore("@256", "D=A", "@SP", "M=D");
    }

    public void Push(String segment, String value) {
        //static, this, local, argument, that, constant, pointer, or temp
        if(segment.equals("constant")) {
            addMore("@" + value, "D=A");
        } else if(segment.equals("static")) {
            var staticSegment = currentFileName + "." + value;
            addMore("@" + staticSegment, "D=M");
        } else {
            var deref = segmentTable.get(segment);
            var aOrM = segment.equals("temp") || segment.equals("pointer") ? "A" : "M";
            addMore("@" + deref, "D=" + aOrM, "@" + value, "A=D+A", "D=M");
        }

        addMore("@SP", "A=M", "M=D");
        increaseStackPointer();
    }

    public void Pop(String segment, String value) {
        if(segment.equals("static")) {
            var staticSegment = currentFileName + "." + value;
            decreaseStackPointer();
            addMore("D=M", "@" + staticSegment, "M=D");
            return;
        }
        var deref = segmentTable.get(segment);
        var aOrM = segment.equals("temp") || segment.equals("pointer") ? "A" : "M";
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

    public void Label(String target) {
        add("(" + target + ")");
    }
    public void Goto(String target) {
        addMore("@" + target, "0;JMP");
    }
    public void IfGoto(String target) {
        addMore("@SP", "AM=M-1", "D=M", "@" + target, "D;JNE");
    }
    public void Call(String function, String argCount) {
        var label = getUniqueLabel();

        // push return address
        addMore("@" + label, "D=A", "@SP", "A=M", "M=D");
        increaseStackPointer();

        // push segments
        for(String segment : Stream.of("LCL", "ARG", "THIS", "THAT").toList()) {
            addMore("@" + segment, "D=M", "@SP", "A=M", "M=D");
            increaseStackPointer();
        }

        // set arg/lcl pointer
        addMore("@SP", "D=M", "@" + argCount, "D=D-A", "@5","D=D-A", "@ARG", "M=D");
        addMore("@SP", "D=M", "@LCL", "M=D");

        Goto(function);
        Label(label);
    }
    public void Function(String function, String argCount) {
        Label(function);

        for(int i = 0; i < Integer.parseInt(argCount); i++) {
            Push("constant", "0");
        }
    }
    public void Return() {
        // set frame and return address
        addMore("@LCL", "D=M", "@FRAME", "M=D", "@5", "A=D-A", "D=M", "@RETURN", "M=D");

        // *ARG = pop
        addMore("@SP", "AM=M-1", "D=M", "@ARG", "A=M", "M=D");

        // restore SP
        addMore("@ARG", "D=M+1", "@SP", "M=D");

        // restore segments
        List<String> segments = Stream.of("THAT", "THIS", "ARG", "LCL").toList();
        for(int i = 0; i < segments.size(); i++) {
            addMore("@FRAME", "D=M", "@" + (i+1), "A=D-A", "D=M", "@" + segments.get(i), "M=D");
        }

        // return
        addMore("@RETURN", "A=M", "0;JMP");
    }

    public void NewFile(String target) {
        this.currentFileName = target;
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
            "@" + labelEnd,
            "0;JMP",
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
