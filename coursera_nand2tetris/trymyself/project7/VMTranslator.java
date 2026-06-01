package coursera_nand2tetris.trymyself.project7;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


enum CommandType {
    C_ARITHMETIC,
    C_PUSH,
    C_POP,
    C_LABEL,
    C_GOTO,
    C_IF,
    C_FUNCTION,
    C_RETURN,
    C_CALL
} 



public class VMTranslator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator <inputfile.vm>");
            return;
        }
        String inputFile = args[0];
        String outputFile = inputFile.replace(".vm", ".asm");

        try {
            CodeWriter codeWriter = new CodeWriter(outputFile);
            Parser parser = new Parser(inputFile);
            while (parser.hasMoreCommands()) {
            parser.advance();
            CommandType commandType = parser.commandType();
            switch (commandType) {
                case C_ARITHMETIC:
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                case C_PUSH:
                case C_POP:
                    codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                    break;
                // Handle other command types as needed
                default:
                    break;
                }
            }
            codeWriter.close();
            System.out.println("Translation completed successfully. Output file: " + outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }
}

class Parser {
    private List<String> lines;
    private int currentIndex;
    private String currentCommand;

   


    // Implementation of the Parser class to read and parse VM commands
    public Parser(String inputFile) throws IOException {
        // Initialize the parser with the input file
        lines = new ArrayList<>();
        String content = Files.readString(Paths.get(inputFile));
        String[] allLines = content.split("\n");
        for (String line : allLines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            line = line.split("//")[0].trim();
            lines.add(line);
        }

        currentIndex = -1;
        currentCommand = null;
    }
    
    public boolean hasMoreCommands() {
        // Return true if there are more commands to process
        return currentIndex < lines.size() - 1;
    }
    
    public void advance() {
        // Read the next command and make it the current command
        currentIndex++;
        currentCommand = lines.get(currentIndex);
    }
    
    public CommandType commandType() {
        // Return the type of the current command
        String[] parts = currentCommand.split("\\s+");
        String cmd = parts[0];

        switch (cmd) {
            case "push": return CommandType.C_PUSH;
            case "pop": return CommandType.C_POP;
            case "add": case "sub": case "neg": case "eq": case "gt": case "lt": case "and": case "or": case "not":
                return CommandType.C_ARITHMETIC;
            // Handle other command types as needed
            default: return null;
        }
    }
    
    public String arg1() {
        // Return the first argument of the current command
        if (commandType() == CommandType.C_ARITHMETIC) {
            return currentCommand.split("\\s+")[0];
        } else {
            return currentCommand.split("\\s+")[1];
        } 
    }
    
    public int arg2() {
        // Return the second argument of the current command (if applicable)
        return Integer.parseInt(currentCommand.split("\\s+")[2]);
    }
}

class CodeWriter {
    private BufferedWriter writer;
    private int labelCount;

    public CodeWriter(String outputFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputFile));
        labelCount = 0;
    }

    public void writeArithmetic(String cmd) throws IOException {
        writer.write("// " + cmd);
        writer.newLine();

        switch (cmd) {
            case "add": writeMath("+"); break;
            case "sub": writeMath("-"); break;
            case "and": writeMath("&"); break;
            case "or":  writeMath("|"); break;
            case "neg": writeUnary("-"); break;
            case "not": writeUnary("!"); break;
            case "eq":  writeCompare("JEQ"); break;
            case "gt":  writeCompare("JGT"); break;
            case "lt":  writeCompare("JLT"); break;
        }
    }

    public void writePushPop(CommandType type, String segment, int index) throws IOException {
        writer.write("// " + (type == CommandType.C_PUSH ? "push" : "pop") + " " + segment + " " + index);
        writer.newLine();

        if (type == CommandType.C_PUSH) {
            writePush(segment, index);
        } else {
            writePop(segment, index);
        }
    }

    private void writePush(String seg, int i) throws IOException {
        String addr = getSegmentAddress(seg, i);
        if (!addr.isEmpty()) {
            write("@" + addr);
            write("D=M");
        }

        pushD();
    }

    private void writePop(String seg, int i) throws IOException {
        String addr = getSegmentAddress(seg, i);

        write("@" + addr);
        write("D=A");
        write("@R13");
        write("M=D");

        popToD();

        write("@R13");
        write("A=M");
        write("M=D");
    }

    private String getSegmentAddress(String seg, int i) {
        switch (seg) {
            case "constant": return String.valueOf(i);
            case "local": return "LCL";
            case "argument": return "ARG";
            case "this": return "THIS";
            case "that": return "THAT";
            case "temp": return "R5";
            case "pointer": return i == 0 ? "THIS" : "THAT";
            default: return "";
        }
    }

    // ====================== 工具方法 ======================
    private void writeMath(String op) throws IOException {
        popToD();
        write("@SP");
        write("A=M-1");
        write("M=M" + op + "D");
    }

    private void writeUnary(String op) throws IOException {
        write("@SP");
        write("A=M-1");
        write("M=" + op + "M");
    }

    private void writeCompare(String jump) throws IOException {
        popToD();
        write("@SP");
        write("A=M-1");
        write("D=M-D");
        write("M=-1");

        String label1 = "LABEL_" + labelCount++;
        String label2 = "LABEL_" + labelCount++;

        write("@" + label1);
        write("D;" + jump);
        write("@SP");
        write("A=M-1");
        write("M=0");
        write("(" + label1 + ")");
    }

    private void pushD() throws IOException {
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    private void popToD() throws IOException {
        write("@SP");
        write("AM=M-1");
        write("D=M");
    }

    private void write(String s) throws IOException {
        writer.write(s);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}