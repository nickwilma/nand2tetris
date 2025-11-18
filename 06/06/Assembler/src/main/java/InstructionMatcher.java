public abstract class InstructionMatcher {

    // c - instruction
    public static boolean isInstructionOrJump(String str) {
        return str.contains("=") || str.contains(";");
    }

    public static boolean isWriteToD(String str) {
        return str.split("=")[0].contains("D");
    }

    public static boolean isWriteToA(String str) {
        return str.split("=")[0].contains("A");
    }

    public static boolean isWriteToM(String str) {
        return str.split("=")[0].contains("M");
    }

    public static boolean isSymbol(String str) {
        return str.contains("@") && !isIntergerParseable(getSymbolName(str));
    }

    public static String getSymbolName(String str) {
        return str.split("@")[1];
    }

    public static boolean isJumpLabel(String str) {
        return str.toCharArray()[0] == '(' && str.toCharArray()[str.length() - 1] == ')';
    }

    public static String getJumpLabel(String str) {
        return str.replace("(", "").replace(")", "");
    }

    public static boolean isRead0(String str) {
        return checkInstructionBy(str,"0");
    }

    public static boolean isRead1(String str) {
        return checkInstructionBy(str,"1");
    }

    public static boolean isReadMinus1(String str) {
        return checkInstructionBy(str,"-1");
    }

    public static boolean isReadD(String str) {
        return checkInstructionBy(str,"D");
    }

    public static boolean isReadA(String str) {
        return checkInstructionBy(str,"A");
    }

    public static boolean isReadNotD(String str) {
        return checkInstructionBy(str,"!D");
    }

    public static boolean isReadNotA(String str) {
        return checkInstructionBy(str,"!A");
    }

    public static boolean isReadMinusD(String str) {
        return checkInstructionBy(str,"-D");
    }

    public static boolean isReadMinusA(String str) {
        return checkInstructionBy(str,"-A");
    }

    public static boolean isReadDPlus1(String str) {
        return checkInstructionBy(str,"D+1");
    }

    public static boolean isReadAPlus1(String str) {
        return checkInstructionBy(str,"A+1");
    }

    public static boolean isReadDMinus1(String str) {
        return checkInstructionBy(str,"D-1");
    }

    public static boolean isReadAMinus1(String str) {
        return checkInstructionBy(str,"A-1");
    }

    public static boolean isReadDPlusA(String str) {
        return checkInstructionBy(str,"D+A");
    }

    public static boolean isReadDMinusA(String str) {
        return checkInstructionBy(str,"D-A");
    }

    public static boolean isReadAMinusD(String str) {
        return checkInstructionBy(str,"A-D");
    }

    public static boolean isReadDAndA(String str) {
        return checkInstructionBy(str,"D&A");
    }

    public static boolean isReadDOrA(String str) {
        return checkInstructionBy(str,"D|A");
    }


    // a - instruction

    public static boolean isReadM(String str) {
        return checkInstructionBy(str,"M");
    }

    public static boolean isReadNotM(String str) {
        return checkInstructionBy(str,"!M");
    }

    public static boolean isReadMinusM(String str) {
        return checkInstructionBy(str,"-M");
    }

    public static boolean isReadMPlus1(String str) {
        return checkInstructionBy(str,"M+1");
    }

    public static boolean isReadMMinus1(String str) {
        return checkInstructionBy(str,"M-1");
    }

    public static boolean isReadDPlusM(String str) {
        return checkInstructionBy(str,"D+M");
    }

    public static boolean isReadDMinusM(String str) {
        return checkInstructionBy(str,"D-M");
    }

    public static boolean isReadMMinusD(String str) {
        return checkInstructionBy(str,"M-D");
    }

    public static boolean isReadDAndM(String str) {
        return checkInstructionBy(str,"D&M");
    }

    public static boolean isReadDOrM(String str) {
        return checkInstructionBy(str, "D|M");
    }

    // instruction helper

    private static boolean checkInstructionBy(String str, String characters) {
        if(str.split("=").length > 1) {
            return str.split("=")[1].equals(characters);
        }
        if(str.contains(";")) {
            return str.split(";")[0].equals(characters);
        }
        return false;
    }

    // jump

    public static boolean isJump(String str) {
        return str.contains(";");
    }

    public static boolean isJGT(String str) {
        return checkJumpBy(str, "JGT");
    }

    public static boolean isJEQ(String str) {
        return checkJumpBy(str, "JEQ");
    }

    public static boolean isJGE(String str) {
        return checkJumpBy(str, "JGE");
    }

    public static boolean isJLT(String str) {
        return checkJumpBy(str, "JLT");
    }

    public static boolean isJNE(String str) {
        return checkJumpBy(str, "JNE");
    }

    public static boolean isJLE(String str) {
        return checkJumpBy(str, "JLE");
    }

    public static boolean isJMP(String str) {
        return checkJumpBy(str, "JMP");
    }

    // jump helper

    private static boolean checkJumpBy(String str, String characters) {
        if(str.split(";").length > 1) {
            return str.split(";")[1].equals(characters);
        }
        return false;
    }

    //
    public static boolean isIntegerLoad(String str) {
        return str.contains("@") && isIntergerParseable(getSymbolName(str));
    }

    public static int getIntValue(String str) {
        return Integer.parseInt(str.split("@")[1]);
    }

    //

    private static boolean isIntergerParseable(String str) {
        //System.out.println("isIntergerParseable: " + str);
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    // a instruction

}
