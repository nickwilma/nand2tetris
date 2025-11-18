import java.util.ArrayList;
import java.util.List;

public class Assembler {

    public Assembler(String filename, String target) {
        String source = Reader.getFileContent(filename);
        List<String> parsedSource = SourceParser.fromString(source);
        SymbolTable symbolTable = new SymbolTable();

        fillSymbolTable(parsedSource, symbolTable);
        List<String> output = parse(parsedSource, symbolTable);

        //System.out.println("parsedSource: " + parsedSource);
        //System.out.println("output " + output);
        //symbolTable.print();
        Writer.writeFileContent(target, output);
        System.out.println("JOB SUCCESSFUL!");
        System.out.println("Your result is under: " + target);
    }

    private void fillSymbolTable(List<String> source, SymbolTable symbolTable) {
        // fill symbol table with jump labels
        for(int i = 0; i < source.size(); i++) {
            String item = source.get(i);
            if(InstructionMatcher.isJumpLabel(item)) {
                String label = InstructionMatcher.getJumpLabel(item);
                symbolTable.add(label, i);
                source.remove(i);
                i--;
            }
        }

        // fill symbol table with symbols
        source.forEach(item -> {
            if(InstructionMatcher.isSymbol(item)) {
                String symbol = InstructionMatcher.getSymbolName(item);
                symbolTable.add(symbol);
            }
        });
    }

    private List<String> parse(List<String> source, SymbolTable symbolTable) {
        List<String> output = new ArrayList<>();
        source.forEach(item -> {
            if(InstructionMatcher.isIntegerLoad(item)) {
                int value = InstructionMatcher.getIntValue(item);
                String result = String.format("0%15s", Integer.toBinaryString(value)).replaceAll(" ", "0");
                output.add(result);
            } else if(InstructionMatcher.isSymbol(item)) {
                String label = InstructionMatcher.getSymbolName(item);
                int value = symbolTable.get(label);
                String result = String.format("0%15s", Integer.toBinaryString(value)).replaceAll(" ", "0");
                output.add(result);
            } else if(InstructionMatcher.isInstructionOrJump(item)) {
                String result = "111";
                //result += "0"; // is c instruction

                // c instructions

                if(InstructionMatcher.isRead0(item)) {
                    result += "0101010";
                }

                if(InstructionMatcher.isRead1(item)) {
                    result += "0111111";
                }

                if(InstructionMatcher.isReadMinus1(item)) {
                    result += "0111010";
                }

                if(InstructionMatcher.isReadD(item)) {
                    result += "0001100";
                }

                if(InstructionMatcher.isReadA(item)) {
                    result += "0110000";
                }

                if(InstructionMatcher.isReadNotD(item)) {
                    result += "0001101";
                }

                if(InstructionMatcher.isReadNotA(item)) {
                    result += "0110001";
                }

                if(InstructionMatcher.isReadMinusD(item)) {
                    result += "0001111";
                }

                if(InstructionMatcher.isReadMinusA(item)) {
                    result += "0110011";
                }

                if(InstructionMatcher.isReadDPlus1(item)) {
                    result += "0011111";
                }

                if(InstructionMatcher.isReadAPlus1(item)) {
                    result += "0110111";
                }

                if(InstructionMatcher.isReadDMinus1(item)) {
                    result += "0001110";
                }

                if(InstructionMatcher.isReadAMinus1(item)) {
                    result += "0110010";
                }

                if(InstructionMatcher.isReadDPlusA(item)) {
                    result += "0000010";
                }

                if(InstructionMatcher.isReadDMinusA(item)) {
                    result += "0010011";
                }

                if(InstructionMatcher.isReadAMinusD(item)) {
                    result += "0000111";
                }

                if(InstructionMatcher.isReadDAndA(item)) {
                    result += "0000000";
                }

                if(InstructionMatcher.isReadDOrA(item)) {
                    result += "0010101";
                }

                // a instructions

                if(InstructionMatcher.isReadM(item)) {
                    result += "1110000";
                }

                if(InstructionMatcher.isReadNotM(item)) {
                    result += "1110001";
                }

                if(InstructionMatcher.isReadMinusM(item)) {
                    result += "1110011";
                }

                if(InstructionMatcher.isReadMPlus1(item)) {
                    result += "1110111";
                }

                if(InstructionMatcher.isReadMMinus1(item)) {
                    result += "1110010";
                }

                if(InstructionMatcher.isReadDPlusM(item)) {
                    result += "1000010";
                }

                if(InstructionMatcher.isReadDMinusM(item)) {
                    result += "1010011";
                }

                if(InstructionMatcher.isReadMMinusD(item)) {
                    result += "1000111";
                }

                if(InstructionMatcher.isReadDAndM(item)) {
                    result += "1000000";
                }

                if(InstructionMatcher.isReadDOrM(item)) {
                    result += "1010101";
                }

                // write

                if(!InstructionMatcher.isJump(item)) {
                    if(InstructionMatcher.isWriteToA(item)) {
                        result += "1";
                    } else {
                        result += "0";
                    }

                    if(InstructionMatcher.isWriteToD(item)) {
                        result += "1";
                    } else {
                        result += "0";
                    }

                    if(InstructionMatcher.isWriteToM(item)) {
                        result += "1";
                    } else {
                        result += "0";
                    }
                } else {
                    result += "000";
                }



                if(InstructionMatcher.isJump(item)) {
                    //result = result.split(result.length() - 3)[0];
                    if(InstructionMatcher.isJGT(item)) {
                        result += "001";
                    } else if(InstructionMatcher.isJEQ(item)) {
                        result += "010";
                    } else if(InstructionMatcher.isJGE(item)) {
                        result += "011";
                    } else if(InstructionMatcher.isJLT(item)) {
                        result += "100";
                    } else if(InstructionMatcher.isJNE(item)) {
                        result += "101";
                    } else if(InstructionMatcher.isJLE(item)) {
                        result += "110";
                    } else if(InstructionMatcher.isJMP(item)) {
                        result += "111";
                    } else {
                        result += "000";
                    }
                } else {
                    result += "000";
                }
                output.add(result);
            }
        });
        return output;
    }
}
