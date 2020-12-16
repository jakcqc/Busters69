import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Arrays;

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
        linesFromFile = removeEmptyLines(linesFromFile);

        for(i = 0; i<linesFromFile.length; i++){
            if(!(linesFromFile[i].isEmpty())){
                if ((linesFromFile[i].charAt(0) != '#')){
                    executeLine(linesFromFile[i]);
                }
            }
        }
        System.out.println(variables);
    }

    public static String[] removeEmptyLines(String [] lines){
        String[] removedNull = Arrays.stream(lines)
                .filter(value ->
                        value != null && value.length() > 0
                )
                .toArray(size -> new String[size]);
        return removedNull;
    }

public static void executeLine(String line){
    
    boolean lineDone = false;
    if (line.contains("while")){
        whileLoop(line);
        i--;
        lineDone = true;
    } 
    if (line.contains("for")){
        forLoopNew(line);
        lineDone = true;
    }
    if (line.contains("if")){
        ifelse(line);
        i--;
        lineDone = true;
    }
    if (line.contains("print")){
        printOut(line);
        lineDone = true;
    }

    if (line.contains("break")){
        lineDone = true;
    }

    if ((lineDone == false) && (line.contains("-=") || line.contains("*=") || line.contains("/=") || line.contains("^=") || line.contains("%=") || line.contains("=") || line.contains("+="))){
        assignmentOperator(line);
        lineDone = true;
    }

    if ((lineDone == false) && (line.contains("-") || line.contains("*") || line.contains("/") || line.contains("^") || line.contains("%") || line.contains("=") || line.contains("="))){
        evaluateArithmatic(line);
        lineDone = true;
    } 
    
    
    
    //makeNewVariable(line);
}

public static void whileLoop(String line){
    int j = 0;
    line = line.replaceAll("[()]",""); 
    int startLineNum = i;
    i++;
    line = linesFromFile[i];
    while(line.charAt(0) == ' ' || line.charAt(0) == '\t'){
        j++;
        i++;
        line = linesFromFile[i];
    }
    while(whileLoopCondition(linesFromFile[startLineNum])){
        i = startLineNum + 1;
        line = linesFromFile[i];
        while((line.charAt(0) == ' ' || line.charAt(0) == '\t')){
            executeLine(line);
            i++;
            line = linesFromFile[i];
            if(linesFromFile[i].isEmpty()){
                break;
            }
        }
        if(linesFromFile[i].isEmpty()){
            break;
        }
    }
    

    i = startLineNum + j + 1;
    line = linesFromFile[i];
}

public static boolean whileLoopCondition(String line){
    boolean result, resultAndOr, evalResult = true;
    String condition, conditionAndOr;
    line = line.replaceAll("[()]","");

    if (line.contains(" and ")){
        condition = line.split("and")[0];
        condition = condition.split("while")[1];
        conditionAndOr = line.split("and")[1];
        conditionAndOr = conditionAndOr.substring(1, conditionAndOr.length()-1);
        result = evaluateTrueFalse(condition);
        resultAndOr = evaluateTrueFalse(conditionAndOr);

        if(result == false || resultAndOr == false){
            evalResult = false;
        }
        else{
            evalResult = true;
        }
    }
    else if (line.contains(" or ")){
        condition = line.split("or")[0];
        condition = condition.split("while")[1];
        conditionAndOr = line.split("or")[1];
        conditionAndOr = conditionAndOr.substring(1, conditionAndOr.length()-1);
        result = evaluateTrueFalse(condition);
        resultAndOr = evaluateTrueFalse(conditionAndOr);
        if(result == false && resultAndOr == false){
            evalResult = false;
        }
        else{
            evalResult = true;
        }
    }
    else{
        if (line.contains("while(") || line.contains("while (")){
            condition = line.split("[\\(\\)]")[1];

        }
        else{
            condition = line.split("while ")[1];
            condition = condition.substring(0, condition.length()-1);
            result = evaluateTrueFalse(condition);
            if(!result){
                evalResult = false;
            }
        }
    }
    return evalResult;
}

public static void forLoop(String line){
    int j = 0;
    int numSpaces = 0;
    int begin = 0;
    int end = 0;
    String temp [];
    String variableName = line.split("for ")[1];
    variableName = variableName.split(" in")[0];
    while (line.charAt(numSpaces) == ' '){
        numSpaces++;
    }
    if (line.contains("range")){
        temp = (range(line).split(":"));
        begin = Integer.parseInt(temp[0]);
        end = Integer.parseInt(temp[1]);
    }


    int startLineNum = i;
    i++;
    line = linesFromFile[i];
    while(line.charAt(numSpaces+3) == ' ' || line.charAt(numSpaces+3) == '\t'){
        i++;
        j++;
        if (i == linesFromFile.length){
            break;
        }
        if(linesFromFile[i].isEmpty() || (linesFromFile[i].length() <= numSpaces+3)){
            break;
        }
        
        line = linesFromFile[i];
    }
    
    while(begin != end){
        variables.put(variableName, Integer.toString(begin));
        i = startLineNum + 1;
        line = linesFromFile[i];
        
        while((line.charAt(numSpaces+3) == ' ' || line.charAt(numSpaces+3) == '\t')){
            executeLine(line);
            i++;
            if(i<linesFromFile.length)
                line = linesFromFile[i];
            else{
                break;
            }
        }
        if(i==linesFromFile.length)
            break;
        if(linesFromFile[i].isEmpty() || (line.length() <= numSpaces+3)){
            break;
        }
        begin++;
    }
    

    i = startLineNum + j +1;
    line = linesFromFile[i];
}

public static void forLoopNew(String line){
    int numSpaces = 0;
    while (line.charAt(numSpaces) == ' '){
        numSpaces++;
    }
    

    int begin = 0;
    int end = 0;
    String temp [];
    String variableName = line.split("for ")[1];
    variableName = variableName.split(" in")[0];
    while (line.charAt(numSpaces) == ' '){
        numSpaces++;
    }
    if (line.contains("range")){
        temp = (range(line).split(":"));
        begin = Integer.parseInt(temp[0]);
        end = Integer.parseInt(temp[1]);
    }
    // ============
    int j = 0;
    int startLineNum = i;
    i++;
    line = linesFromFile[i];
    while(line.charAt(0) == ' ' || line.charAt(0) == '\t'){
        j++;
        i++;
        line = linesFromFile[i];
    }
    while(begin != end ){
        variables.put(variableName, Integer.toString(begin));
        i = startLineNum + 1;
        line = linesFromFile[i];
        while((line.charAt(0) == ' ' || line.charAt(0) == '\t')){
            executeLine(line);
            i++;
            line = linesFromFile[i];
            if(linesFromFile[i].isEmpty()){
                break;
            }
        }
        if(linesFromFile[i].isEmpty()){
            break;
        }
        begin++;
    }
    

    i = startLineNum + j + 1;
    line = linesFromFile[i];
}

public static String range(String line){
    // line = line.replaceAll("[()]",""); 
    String parse = line.split("range\\(")[1];
    
    parse = parse.substring(0, parse.length()-2);
    parse = parse.replaceAll("\\s", "");

    String first = parse.split(",")[0];
    String second = parse.split(",")[1];
    ArrayList<String> entries = new ArrayList<String>();

    variables.forEach((k, v) -> {
        entries.add(k);
    });
    boolean firstFlag = false;
    boolean secondFlag = false;
    if (first.contains("int")){
        first = first.replaceAll("[()]",""); 
        first = first.replaceAll("int", "");
        firstFlag = true;
    }
    if (second.contains("int")){
        second = second.replaceAll("[()]",""); 
        second = second.replaceAll("int", "");
        secondFlag = true;
    }

    for (int k = 0; k < entries.size(); k++){
        if(first.contains(entries.get(k))){
            first = first.replaceAll(entries.get(k), variables.get(entries.get(k)));
        }
        if(second.contains(entries.get(k))){
            second = second.replaceAll(entries.get(k), variables.get(entries.get(k)));
        }
    }


    if (firstFlag){
        // first = first.replaceAll("[()]",""); 
        // first = first.replaceAll("int", "");
        first = Integer.toString(evaluateArithmaticReturn(first));
    }
    
    if (secondFlag){
        // second = second.replaceAll("[()]",""); 
        // second = second.replaceAll("int", "");
        second = Integer.toString(evaluateArithmaticReturn(second));
    }
    String returnString = first+":"+second;
    return returnString;
}

public static void makeNewVariable(String variableName, String variableValue){
    variables.put(variableName, variableValue);
}

public static void updateVariable(String line, String variableName, String variableValue){
    if ((line.contains("-") || line.contains("*") || line.contains("/") || line.contains("^") || line.contains("%") || line.contains("+"))){
        evaluateArithmatic(line);
    } 
    else {
        makeNewVariable(variableName, variableValue);
    }
    
}

public static boolean checkIfExists(String variableName){
    return variables.containsKey(variableName);
}

public static boolean assignmentOperator(String line){
    line = line.replaceAll("[()]","");    
    String variableName = line.split("=")[0];
    variableName = variableName.replaceAll("\\s","");
    String variableValue = line.split("=")[1];
    if(variableValue.charAt(0) == ' '){ // Gets rid of space if present between = and new value
        variableValue = variableValue.substring(1, variableValue.length());
    }
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
            updateVariable(line, variableName, variableValue);
        }

    }
    else{
        if (!Character.isDigit(variableValue.charAt(0)) && variableValue.charAt(0) != '-'){
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
            updateVariable(line, variableName, variableValue);
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
    variables.put(variable, String.valueOf(oldVariableValue + Double.parseDouble(value)));
}

public static void intMinusEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    variables.put(variable, String.valueOf(oldVariableValue - Double.parseDouble(value)));
}

public static void intMultEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    variables.put(variable, String.valueOf(oldVariableValue * Double.parseDouble(value)));
}

public static void intDivEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    variables.put(variable, String.valueOf(oldVariableValue / Double.parseDouble(value)));
}

public static void intExpEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    Double newValue = Math.pow(oldVariableValue, Double.parseDouble(value));
    variables.put(variable, String.valueOf(newValue));
}

public static void intModEquals(String variable, String value){
    Double oldVariableValue = Double.parseDouble(variables.get(variable));
    variables.put(variable, String.valueOf(oldVariableValue % Double.parseDouble(value)));
}



public static void ifelse(String line){
    int j = 0;
    boolean result, resultAndOr, evalResult = true;
    String condition, conditionAndOr;
    // line = line.replaceAll("[()]","");  
    // if (line.charAt(line.length()-1) != ':'){
    //     // Syntax Error
    //     System.out.println("Syntax Error 194");
    //     System.exit(0);
    // }
    if (line.contains(" and ")){
        line = line.replaceAll("[()]","");
        condition = line.split("and")[0];
        condition = condition.split("if")[1];
        conditionAndOr = line.split("and")[1];
        conditionAndOr = conditionAndOr.substring(1, conditionAndOr.length()-1);
        result = evaluateTrueFalse(condition);
        resultAndOr = evaluateTrueFalse(conditionAndOr);

        if(result == false || resultAndOr == false){
            evalResult = false;
        }
        else{
            evalResult = true;
        }
    }
    else{ 
        if (line.contains(" or ")){
            line = line.replaceAll("[()]","");
            condition = line.split("or")[0];
            condition = condition.split("if")[1];
            conditionAndOr = line.split("or")[1];
            conditionAndOr = conditionAndOr.substring(1, conditionAndOr.length()-1);
            result = evaluateTrueFalse(condition);
            resultAndOr = evaluateTrueFalse(conditionAndOr);
            if(result == false && resultAndOr == false){
                evalResult = false;
            }
            else{
                evalResult = true;
            }
        }
        else{
            if (line.contains("if(") || line.contains("if (")){
                condition = line.split("[\\(\\)]")[1];
            }
            else{
                line = line.replaceAll("[()]","");
                condition = line.replaceAll("\\s", "");
                if(line.contains(("elif"))){
                    condition = condition.substring(4, condition.length()-1);
                }
                else{
                    condition = condition.substring(2, condition.length()-1);
                }
            }
                result = evaluateTrueFalse(condition);
                if(!result){
                    evalResult = false;
            }
        }
    }
    
    int ifSpacing = 0;
        while(line.charAt(ifSpacing) == ' '){
            ifSpacing++;
        }
    i++;
    line = linesFromFile[i];
    if(evalResult){
        while(line.charAt(ifSpacing+3) == ' ' || line.charAt(ifSpacing+3) == ('\t')){
            executeLine(line);
            i++;
            j++;
            if(i < numberOfLines)
                line = linesFromFile[i];
            else
                break;
            if(linesFromFile[i].isEmpty()){
                break;
            }
        }
        if(j == 0){
            // Syntax Error
            System.out.println("Syntax Error332");
            System.exit(0);
        }
        if(j > 0){
            if(line.contains("elif")){
                i++;
                line = linesFromFile[i];
                j = 0;
                while(line.charAt(ifSpacing+3) == ' ' || line.charAt(ifSpacing+3) == ('\t')){
                    i++;
                    j++;
                    line = linesFromFile[i];
                    if(linesFromFile[i].isEmpty() || (line.length() <= ifSpacing+3)){
                        break;
                    }
                }
                if(j == 0){
                    // Syntax Error
                    System.out.println("Syntax Error 247");
                    
                }
            }
            if(line.contains("else:")){
                i++;
                line = linesFromFile[i];
                j = 0;
                while(line.charAt(ifSpacing+3) == ' ' || line.charAt(ifSpacing+3) == ('\t')){
                    i++;
                    j++;
                    line = linesFromFile[i];
                    if(linesFromFile[i].isEmpty() || (line.length() <= ifSpacing+3)){
                        break;
                    }
                }
                if(j == 0){
                    // Syntax Error
                    System.out.println("Syntax Error 362");
                    
                }
            }
        }
    }
    else{
        j = 0;
        while(line.charAt(ifSpacing+2) == ' ' || line.charAt(ifSpacing+2) == ('\t')){
            i++;
            j++;
            if (i+1 == linesFromFile.length){
                break;
            }
            if(linesFromFile[i].isEmpty() || (line.length() <= ifSpacing+3)){
                break;
            }
            line = linesFromFile[i];
            
        }
        if(j == 0){
            // Syntax Error
            System.out.println("Syntax Error 378");
        }
        if(line.contains("elif")){
            ifelse(line);
         }
        if(line.contains("else:")){
            i++;
            line = linesFromFile[i];
            j = 0;
            while(line.charAt(ifSpacing+3) == ' ' || line.charAt(ifSpacing+3) == ('\t')){
                executeLine(line);
                i++;
                j++;
                line = linesFromFile[i];
                if(linesFromFile[i].isEmpty() || (line.length() <= ifSpacing+3)){
                    break;
                }
            }
            if(j == 0){
                // Syntax Error
                System.out.println("Syntax Error 395");
                System.exit(0);
            }
        }
    }
}

public static void printOut(String line){
    String temp1;
    String temp2;
    String result = "";
    int index;
    if(line.contains("str")){
        temp1 = line.split("str\\(")[0];
        temp2 = line.split("str\\(")[1];
        index = temp2.indexOf(")");
        if (index == -1)
        {
            
        }
        else
        {
            result = temp2.substring(0, index) + temp2.substring(index);
        }
        line = temp1 + result;
        
    } 
    String statement = "";
    for(int p = 1; p < line.split("[\\(\\)]").length; p++){
        statement += line.split("[\\(\\)]")[p];
    }
    //String statement = line.split("[\\(\\)]")[1];
    
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
            if (variablesInput[0].contains("%")){
                String temp1 = variablesInput[0].split("%")[0];
                String temp2 = variablesInput[0].split("%")[1];
                double temp3 = Double.parseDouble(variables.get(temp1));
                double temp4 = Double.parseDouble(variables.get(temp2));
                first = temp3 % temp4;
            }
            else
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
    ScriptException error;

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");        
    try {
        result = engine.eval(expression);
    } catch (ScriptException e) {
        error = e;
    }
    variables.put(variable, result.toString());
}

public static int evaluateArithmaticReturn(String line){
    line = line.replaceAll("\\s","");
    Object result = 0;
    ScriptException error;

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");        
    try {
        result = engine.eval(line);
    } catch (ScriptException e) {
        error = e;
    }
    String temp = result.toString();
    int tempChar = temp.indexOf(".");
    if(tempChar != -1){
        temp = temp.substring(0, tempChar);
    }
    //variables.put(variable, result.toString());
    return Integer.parseInt(temp);
}

}