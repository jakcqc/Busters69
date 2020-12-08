import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


class Main {
    public static ArrayList<String> pythonKeywords = new ArrayList();
    public static int numberOfLines;
    public static int i;
    public static String[] linesFromFile;
    public static HashMap<String, String> variables = new HashMap<>();
    public static void main(String[] args) throws ScriptException {
        initializeKeywords();
        
        String in = "";
        try {
            in = Files.readString(Path.of("test2.py"));
        } catch (IOException e){
            System.out.println("Could not open file");
        }

        linesFromFile = in.split("\n");
        numberOfLines = linesFromFile.length;
        //i = 0;

        System.out.println("FLAG: " + linesFromFile[12]);
        for(i = 0; i<linesFromFile.length; i++){
            if(!(linesFromFile[i].isEmpty())){
                if ((linesFromFile[i].charAt(0) != '#')){
                    System.out.println("Running line: "+(i+1));
                    executeLine(linesFromFile[i]);
                    System.out.println("\n");
                }
            }
        }
        System.out.println(variables);

    }

public static void executeLine(String line){
    boolean lineDone = false;
    if (line.contains("while")){
        // Call print function
        lineDone = true;
    } 
    if (line.contains("for")){
        // Call print function
        lineDone = true;
    }
    if (line.contains("if")){
        
        ifelse(line);
        lineDone = true;
    }
    if (line.contains("print")){
        printOut(line);
        lineDone = true;
    }

    if ((lineDone == false) && (line.contains("-=") || line.contains("*=") || line.contains("/=") || line.contains("^=") || line.contains("%=") || line.contains("=") || line.contains("+="))){
        System.out.println(assignmentOperator(line));
        lineDone = true;
    }

    if ((lineDone == false) && (line.contains("-") || line.contains("*") || line.contains("/") || line.contains("^") || line.contains("%") || line.contains("=") || line.contains("="))){
        evaluateArithmatic(line);
        lineDone = true;
    } 

    
    //makeNewVariable(line);
}

public static void makeNewVariable(String line){
    line = line.replaceAll("\\s","");
    line = line.replaceAll("[()]","");

    String variableName = line.split("=")[0];
    String variableValue = line.split("=")[1];
    variables.put(variableName, variableValue);
}

public static void updateVariable(String line){
    if ((line.contains("-") || line.contains("*") || line.contains("/") || line.contains("^") || line.contains("%") || line.contains("+"))){
        evaluateArithmatic(line);
    } 
    else {
        makeNewVariable(line);
    }
    
}

public static boolean checkIfExists(String variableName){
    return variables.containsKey(variableName);
}

public static boolean assignmentOperator(String line){
    line = line.replaceAll("\\s","");
    line = line.replaceAll("[()]","");    
    String variableName = line.split("=")[0];
    String variableValue = line.split("=")[1];
    boolean toggle = false;
    
    if(variableValue.charAt(0)== '"'){ // If string section
        if (line.contains("+=")){
            toggle = true;
            stringAppend(variableName, variableValue);
        }
        if (line.contains("-=") || line.contains("*=") || line.contains("/=") || line.contains("^=") || line.contains("%=")){
            toggle = true;
            System.out.println("Error at line "+i);
        }
        if (toggle == false){
            updateVariable(line);
        }

    }
    else{
        if (!(Character.isDigit(variableValue.charAt(0)))){
            variableValue = (variables.get(variableValue));
        }
        if (line.contains("+=")){
            intPlusEquals(variableName.substring(0, variableName.length()-1), variableValue);
            return true;
        }
        if (line.contains("-=")){
            intMinusEquals(variableName.substring(0, variableName.length()-1), variableValue);
            return true;
        }
        if (line.contains("*=")){
            intMultEquals(variableName.substring(0, variableName.length()-1), variableValue);
            return true;
        }
        if (line.contains("/=")){
            intDivEquals(variableName.substring(0, variableName.length()-1), variableValue);
            return true;
        }
        if (line.contains("^=")){
            intExpEquals(variableName.substring(0, variableName.length()-1), variableValue);
            return true;
        }
        if (line.contains("%=")){
            intModEquals(variableName.substring(0, variableName.length()-1), variableValue);
            return true;
        }
        if (line.contains("=")){
            updateVariable(line);
            return true;
        }
    }

    
    return false;
}

public static void stringAppend(String variable, String stringToAppend){
    variable = variable.substring(0, variable.length()-1); //Cuts the "+" off of the variable name
    String newString = variables.get(variable); // Gets the original value of the variable "variableValue"
    newString = newString.substring(0, newString.length()-1); //Cuts off last quote "variableValue
    newString = newString + stringToAppend.substring(1); //Appends new value without its leading quote
    variables.put(variable, newString);
}

public static void intPlusEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    System.out.println(oldVariableValue);
    variables.put(variable, String.valueOf(oldVariableValue + Double.parseDouble(value)));
}

public static void intMinusEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    System.out.println(oldVariableValue);
    variables.put(variable, String.valueOf(oldVariableValue - Double.parseDouble(value)));
}

public static void intMultEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    System.out.println(oldVariableValue);
    variables.put(variable, String.valueOf(oldVariableValue * Double.parseDouble(value)));
}

public static void intDivEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    System.out.println(oldVariableValue);
    variables.put(variable, String.valueOf(oldVariableValue / Double.parseDouble(value)));
}

public static void intExpEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    System.out.println(oldVariableValue);
    Double newValue = Math.pow(oldVariableValue, Double.parseDouble(value));
    variables.put(variable, String.valueOf(newValue));
}

public static void intModEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    System.out.println(oldVariableValue);
    variables.put(variable, String.valueOf(oldVariableValue % Double.parseDouble(value)));
}



public static void ifelse(String line){
    int j = 0;
    boolean result, resultAndOr, evalResult = true;
    String condition, conditionAndOr;
    // if (line.charAt(line.length()-1) != ':'){
    //     // Syntax Error
    //     System.out.println("Syntax Error 194");
    //     System.exit(0);
    // }
    if (line.contains(" and ")){
        condition = line.split("[\\(\\)(\\)")[1];
        conditionAndOr = line.split("[\\(\\)(\\)")[2];
        result = evaluateTrueFalse(condition);
        resultAndOr = evaluateTrueFalse(conditionAndOr);

        if(!result && resultAndOr){
            evalResult = false;
        }
    }
    else if (line.contains(" or ")){
        condition = line.split("[\\(\\)(\\)")[1];
        conditionAndOr = line.split("[\\(\\)(\\)")[2];
        result = evaluateTrueFalse(condition);
        resultAndOr = evaluateTrueFalse(conditionAndOr);

        if(result && resultAndOr){
            evalResult = false;
        }
    }
    else{
        if (line.contains("if(") || line.contains("if (")){
            condition = line.split("[\\(\\)]")[1];

        }
        else
            condition = line.substring(3, line.length()-1);
        System.out.println("HERE: "+condition);
        //condition = line.split("[\\ \\]")[1];
        result = evaluateTrueFalse(condition);
        if(!result){
            evalResult = false;
        }
    }
    System.out.println("HERE : "+condition);
    i++;
    if(evalResult){
        line = linesFromFile[i];
        while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
            executeLine(line);
            i++;
            j++;
            if(i < numberOfLines)
                line = linesFromFile[i];
            else
                break;
        }
        if(j == 0){
            // Syntax Error
            System.out.println("Syntax Error236");
            System.exit(0);
        }
        if(j > 0){
            if(line.contains("elif")){
                i++;
                line = linesFromFile[i];
                j = 0;
                while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                    i++;
                    j++;
                    line = linesFromFile[i];
                }
                if(j == 0){
                    // Syntax Error
                    System.out.println("Syntax Error 277");
                    
                }
            }
            if(line.contains("else:")){
                i++;
                line = linesFromFile[i];
                j = 0;
                while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                    i++;
                    j++;
                    line = linesFromFile[i];
                }
                if(j == 0){
                    // Syntax Error
                    System.out.println("Syntax Error 251");
                    
                }
            }
        }
    }
    else{
        line = linesFromFile[i];
        j = 0;
        while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
            i++;
            j++;
            line = linesFromFile[i];
        }
        if(j == 0){
            // Syntax Error
            System.out.println("Syntax Error 264");
        }
        if(line.contains("elif")){
            if (line.contains("elif(") || line.contains("elif (")){
                condition = line.split("[\\(\\)]")[1];
            }
            condition = line.substring(5, line.length()-1);
            result = evaluateTrueFalse(condition);
            i++;
            if(result){
                line = linesFromFile[i];
                j = 0;
                while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                    executeLine(line);
                    i++;
                    j++;
                    line = linesFromFile[i];
                }
                if(j == 0){
                    // Syntax Error
                    System.out.println("Syntax Error 313");
                    System.exit(0);
                }
                if(j > 0){
                    //i++;
                    //line = linesFromFile[i];
                    if(line.contains("else:")){
                        i++;
                        line = linesFromFile[i];
                        j = 0;
                        while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                            i++;
                            j++;
                            line = linesFromFile[i];
                        }
                        if(j == 0){
                            // Syntax Error
                            System.out.println("Syntax Error 330");
                            
                        }
                    }
                }
            }
            else{
                line = linesFromFile[i];
                j = 0;
                while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                    i++;
                    j++;
                    line = linesFromFile[i];
                }
                if(j == 0){
                    // Syntax Error
                    System.out.println("Syntax Error 347");
                    
                }
                if(line.contains("else:")){
                    i++;
                    line = linesFromFile[i];
                    j = 0;
                    while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                        executeLine(line);
                        i++;
                        j++;
                        line = linesFromFile[i];
                    }
                    if(j == 0){
                        // Syntax Error
                        System.out.println("Syntax Error 361");
                        System.exit(0);
                    }
                }
            }
        }
        if(line.contains("else:")){
            i++;
            line = linesFromFile[i];
            j = 0;
            while(line.charAt(0) == ' ' || line.charAt(0) == ('\t')){
                executeLine(line);
                i++;
                j++;
                line = linesFromFile[i];
            }
            if(j == 0){
                // Syntax Error
                System.out.println("Syntax Error 275");
                System.exit(0);
            }
        }
    }
    i--;
}

public static void printOut(String line){
    String statement = line.split("[\\(\\)]")[1];
    String statements[] = statement.split("\\+");
    for(int x = 0; x < statements.length; x++){
        if (statements[x].charAt(0) == '"'){
            statements[x] = statements[x].substring(1, statements[x].length()-1);
            System.out.print(statements[x]);
        }
        else{
            statements[x] = variables.get(statements[x]);
            System.out.print(statements[x].replaceAll("\"", ""));
        }
    }
    System.out.println();
    
}

public static void initializeKeywords(){
    pythonKeywords.add("and");
    pythonKeywords.add("as");
    pythonKeywords.add("assert");
    pythonKeywords.add("break");
    pythonKeywords.add("class");
    pythonKeywords.add("continue");
    pythonKeywords.add("def");
    pythonKeywords.add("elif");
    pythonKeywords.add("else");
    pythonKeywords.add("except");
    pythonKeywords.add("False");
    pythonKeywords.add("finally");
    pythonKeywords.add("for");
    pythonKeywords.add("from");
    pythonKeywords.add("global");
    pythonKeywords.add("if");
    pythonKeywords.add("import");
    pythonKeywords.add("in");
    pythonKeywords.add("is");
    pythonKeywords.add("lambda");
    pythonKeywords.add("None");
    pythonKeywords.add("nonlocal");
    pythonKeywords.add("not");
    pythonKeywords.add("or");
    pythonKeywords.add("pass");
    pythonKeywords.add("raise");
    pythonKeywords.add("return");
    pythonKeywords.add("True");
    pythonKeywords.add("try");
    pythonKeywords.add("while");
    pythonKeywords.add("with");
    pythonKeywords.add("yield");
}

public static int incJ(int j){
    if (j < numberOfLines-1)
        j++;
    else
        System.exit(0);
    return j;
}

public static boolean evaluateTrueFalse(String line){
    line = line.replaceAll("\\s","");
    String[] variablesInput;
    double first, second;
    
    if (line.contains("<=")){
        variablesInput = line.split("<=");
        if (Character.isDigit(variablesInput[0].charAt(0))){
            first = Double.parseDouble(variablesInput[0]);
        }
        else{
            first = Double.parseDouble(variables.get(variablesInput[0]));
        }
        if (Character.isDigit(variablesInput[1].charAt(0))){
            second = Double.parseDouble(variablesInput[1]);
        }
        else{
            second = Double.parseDouble(variables.get(variablesInput[1]));
        }
        return first<=second;
    }
    if (line.contains(">=")){
        variablesInput = line.split(">=");
        if (Character.isDigit(variablesInput[0].charAt(0))){
            first = Double.parseDouble(variablesInput[0]);
        }
        else{
            first = Double.parseDouble(variables.get(variablesInput[0]));
        }
        if (Character.isDigit(variablesInput[1].charAt(0))){
            second = Double.parseDouble(variablesInput[1]);
        }
        else{
            second = Double.parseDouble(variables.get(variablesInput[1]));
        }
        return first>=second;
    }
    if (line.contains(">")){
        variablesInput = line.split(">");
        if (Character.isDigit(variablesInput[0].charAt(0))){
            first = Double.parseDouble(variablesInput[0]);
        }
        else{
            first = Double.parseDouble(variables.get(variablesInput[0]));
        }
        if (Character.isDigit(variablesInput[1].charAt(0))){
            second = Double.parseDouble(variablesInput[1]);
        }
        else{
            second = Double.parseDouble(variables.get(variablesInput[1]));
        }
        return first>second;
    }
    if (line.contains("<")){
        variablesInput = line.split("<");
        
        if (Character.isDigit(variablesInput[0].charAt(0))){
            first = Double.parseDouble(variablesInput[0]);
        }
        else{
            first = Double.parseDouble(variables.get(variablesInput[0]));
        }
        if (Character.isDigit(variablesInput[1].charAt(0))){
            second = Double.parseDouble(variablesInput[1]);
        }
        else{
            second = Double.parseDouble(variables.get(variablesInput[1]));
        }

        return first<second;
    }
    if (line.contains("==")){
        variablesInput = line.split("==");
        if (Character.isDigit(variablesInput[0].charAt(0))){
            first = Double.parseDouble(variablesInput[0]);
        }
        else{
            first = Double.parseDouble(variables.get(variablesInput[0]));
        }
        if (Character.isDigit(variablesInput[1].charAt(0))){
            second = Double.parseDouble(variablesInput[1]);
        }
        else{
            second = Double.parseDouble(variables.get(variablesInput[1]));
        }
        return first==second;
    }
    if (line.contains("!=")){
        variablesInput = line.split("!=");
        if (Character.isDigit(variablesInput[0].charAt(0))){
            first = Double.parseDouble(variablesInput[0]);
        }
        else{
            first = Double.parseDouble(variables.get(variablesInput[0]));
        }
        if (Character.isDigit(variablesInput[1].charAt(0))){
            second = Double.parseDouble(variablesInput[1]);
        }
        else{
            second = Double.parseDouble(variables.get(variablesInput[1]));
        }
        return first!=second;
    }
    return false;
}

public static void evaluateArithmatic(String line){
    line = line.replaceAll("\\s","");
    String variable = line.split("=")[0];
    String expression = line.split("=")[1];
    Object result = 0;

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");        
    try {
        result = engine.eval(expression);
    } catch (ScriptException e) {
        System.out.println(e);
    }
    variables.put(variable, result.toString());
}

}
