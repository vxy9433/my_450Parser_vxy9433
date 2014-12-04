package edu.louisiana.cacs.csce450Project;																																			

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author VenkataPradeep
 */


import java.util.*;
//import Tree.dir;

public class Parser {
    /*
	* YOUR CODE GOES HERE
	* 
	* You must implement two methods
	* 1. parse
	* 2. printParseTree
     
    * Print the intermediate states of the parsing process,
    * including the intermediate states of the parse tree,make
    * as specified in the class handout.
    * If the input is legal according to the grammar,
    * print ACCEPT, else UNGRAMMATICAL.
    * If the parse is successful, print the final parse tree.

    * You can modify the input and output of these function but not the name
    */
    
    //Global Variables
    static LinkedList<String> input = new LinkedList<String>(); //Queue to store input string
    static Stack<String> stack = new Stack<String>(); //Stack to store parsing table 
    static String formattedInput = new String(); //Queue to store formatted input string
    static String formattedStack = new String(); //Stack to store formatted parsing table 
    static String actionValue = new String(); //Stores action value
    static String goToValue = null; // stores goto value
    static String tempActionLookup = new String(); //temp variable to store action lookup
    static String tempGotoLookup = new String(); //temp variable to store goto lookup
    static String tempStackAction = new String(); //temp variable to store stack action
    static Stack<String> parseTreeStack = new Stack<String>(); //temp variable to store parse tree
    static String formattedParseTreeStack = new String(); //Stack to store formatted parse tree stack table
    static Stack<String> tempParseStack = new Stack<String>(); //temp list to store parse stack
    static String tempParseTreeString = new String();
    static String rule = new String(); //stores rule to be used
    static String LHS = new String(); //stores LHS of rule
    static int lengthRHS = 0; //stores length of RHS 
    // array to store given rules
    static final String rules[] = {"E->E+T", "E->T", "T->T*F", "T->F", "F->(E)", "F->id"};
    
    //Action Table
    static final String action[][] = { {"S5", "", "", "S4", "", ""},
                                {"", "S6", "", "", "", "accept"},
                                {"", "R2", "S7", "", "R2", "R2"},
                                {"", "R4", "R4", "", "R4", "R4"},
                                {"S5", "", "", "S4", "", ""},
                                {"", "R6", "R6", "", "R6", "R6"},
                                {"S5", "", "", "S4", "", ""},
                                {"S5", "", "", "S4", "", ""},
                                {"","S6","","","S11","",},
                                {"","R1","S7","","R1","R1",},
                                {"","R3","R3","","R3","R3",},
                                {"","R5","R5","","R5","R5",}
                              };
    //Goto Table
    static final String goTo[][] = { {"1","2","3"},
                        {"","",""},
                        {"","",""},
                        {"","",""},
                        {"8","2","3"},
                        {"","",""},
                        {"","9","3"},
                        {"","","10"},
                        {"","",""},
                        {"","",""},
                        {"","",""},
                        {"","",""}
                      };
    
    // Function to parse the given string
    public Parser(String fileName){
        //System.out.println("File to parse : "+fileName);        
        Scanner s = new Scanner(fileName);
        input.addAll(s.inputList);
        stack.push("0"); //Intial stack configuration
        //prints names of columns in the output
        System.out.printf("Stack\t\t\t    Input \t\t    Action    Action    Value   Length  Temp \t\t     Goto  \t  Goto    Stack     Parse Tree Stack\n" );
        System.out.printf("     \t\t\t    Tokens\t\t    Lookup    Value     of LHS  of RHS  stack\t\t     Lookup\t  Value   Action                    \n" );
        System.out.printf("__________________________________________________________________________________________________________________________________________________________________________________________________________________________________\n" );
    }
    
    //Function to parse given string
    public void parse(){
	try{        
        while(!(input.peek().equalsIgnoreCase("$")) || !(actionValue.equalsIgnoreCase("accept"))){ // Loops until input reduces to $ and action value = accept
            if (stack.size() == 1) // Initial config of parser
                actionLookup(Integer.parseInt(stack.peek()), input.peek());
            else{ 
                if (goToValue != null && ! goToValue.equalsIgnoreCase("")) //If goto value is not null then Sm = goto value, else Sm is from stack
                    actionLookup(Integer.parseInt(goToValue), input.peek());
                else{
                    if(stack.peek().length()==3 && !((stack.peek()).substring((stack.peek()).length()-2, stack.peek().length()-1).equalsIgnoreCase("d")))
                        actionLookup(Integer.parseInt((stack.peek()).substring((stack.peek()).length()-2)), input.peek());
                    else if(stack.peek().length()==3 && ((stack.peek()).substring((stack.peek()).length()-2, stack.peek().length()-1).equalsIgnoreCase("d")))
                        actionLookup(Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)), input.peek());
                    else    
                        actionLookup(Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)), input.peek());
                }
            }
            if ((actionValue.charAt(0)) == 'S'){ // If action value is S then do shift, else do reduce
                shift(input.peek());
            }
            else if ((actionValue.charAt(0)) == 'R'){
                reduce(actionValue);
            }
            else if (actionValue.equalsIgnoreCase("accept")){ //If action value = accept then print values and terminate the program
                formattedStack = removeSpacesBraces(stack.toString());
                formattedInput = removeSpacesBraces(input.toString());
                System.out.printf("%-27s %-24s", formattedStack, formattedInput);// Prints Parse Stack and input tokens
                if (goToValue != null && ! goToValue.equalsIgnoreCase("")){ //Loop to print action lookup
                    tempActionLookup += "[ "+ Integer.parseInt(goToValue) + "," + input.peek() + "]";
                    System.out.printf("%-12s", tempActionLookup);
                    //System.out.printf("[ %s, %s]", Integer.parseInt(goToValue), input.peek());
                }
                else{
                    tempActionLookup += "[ "+ Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)) + "," + input.peek() + "]";
                    System.out.printf("%-12s", tempActionLookup);
                }
                System.out.printf("%-76s",actionValue); //Prints action value
                System.out.printf("%-50s\n", formattedParseTreeStack);// Prints parse tree stack
            }      
            tempActionLookup = ""; //clears action lookup
        }
        System.out.printf("\n Parse Tree:\n");
        printParseTree();
        } catch (Exception e){
		System.out.println("Invalid Parse Input");
		}

	
    }
    
    // Function to do shifting operation
    public static void shift(String X){                
        formattedStack = removeSpacesBraces(stack.toString());
        formattedInput = removeSpacesBraces(input.toString());
        System.out.printf("%-27s %-24s", formattedStack, formattedInput);// Prints Parse Stack and input tokens
        if (goToValue != null && ! goToValue.equalsIgnoreCase("")){ //Loop to print action lookup
            tempActionLookup += "[ "+ Integer.parseInt(goToValue) + "," + input.peek() + "]";
            System.out.printf("%-12s", tempActionLookup);       
        }
        else{
                tempActionLookup += "[ "+ Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)) + "," + input.peek() + "]";
                System.out.printf("%-12s", tempActionLookup);
        }
        System.out.printf("%-66s",actionValue); //To print action value
        //tempParseList.add(X);
        //tempParseList.add(Character.toString(actionValue.charAt(1)));
        if (actionValue.length() == 3 ){
            stack.push(X+ (Character.toString(actionValue.charAt(1))) + (Character.toString(actionValue.charAt(2)))); //to push items onto stack
        }
        else
            stack.push(X.concat(Character.toString(actionValue.charAt(1)))); //to push items onto stack
        if (actionValue.length() == 3 )
            tempStackAction = "push "+X.concat(Character.toString(actionValue.charAt(1)))+(Character.toString(actionValue.charAt(2))); //stack action
        else
            tempStackAction = "push "+X.concat(Character.toString(actionValue.charAt(1)));
        System.out.printf("%-10s",tempStackAction); //to print stack action
        printParseTreeStack(actionValue, tempActionLookup, LHS);
        input.remove(); //to remove items from input queue
        formattedParseTreeStack = getReverseStack(parseTreeStack);
        System.out.printf("%-50s\n", formattedParseTreeStack);
        //clear global variables
        goToValue = "";
        tempActionLookup = "";
        tempGotoLookup = "";
        tempStackAction = "";
        //parseTreeStack.clear();
    }
    
    public static void reduce(String action){
        formattedStack = removeSpacesBraces(stack.toString());
        formattedInput = removeSpacesBraces(input.toString());
        System.out.printf("%-27s %-24s", formattedStack, formattedInput);// Prints Parse Stack and input tokens
        if (goToValue != null && ! goToValue.equalsIgnoreCase("")){ //Loop to print action lookup
            tempActionLookup += "[ "+ Integer.parseInt(goToValue) + "," + input.peek() + "]";
            System.out.printf("%-12s", tempActionLookup);
            //System.out.printf("[ %s, %s]", Integer.parseInt(goToValue), input.peek());
        }
        else{
            if(stack.peek().length()==3 && !((stack.peek()).substring((stack.peek()).length()-2, stack.peek().length()-1).equalsIgnoreCase("d"))){
                tempActionLookup += "[ "+ Integer.parseInt((stack.peek()).substring((stack.peek()).length()-2)) + "," + input.peek() + "]";
            }
            else         
                tempActionLookup += "[ "+ Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)) + "," + input.peek() + "]";
            System.out.printf("%-12s", tempActionLookup);
        }
        rule = rules[Integer.parseInt(action.substring(1))-1]; //to get specific rule from rules table
        LHS = rule.substring(0,1); // LHS of rule
        if (Integer.parseInt(action.substring(1)) == 6) //Loop to get length of RHS
            lengthRHS = 1;
        else{            
            lengthRHS = (rule.length())-3;            
        }
        //Loop to pop items based on RHS length
        for (int i=1; i<= lengthRHS; i++){
            if (!stack.peek().equalsIgnoreCase("0"))
                stack.pop();               
        }
        
        //to lookup in goto table
        goToLookup(Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)), LHS);
        
        System.out.printf("%-9s %-7s %-6s",actionValue, LHS, lengthRHS); //To print action value LHS and RHS
        formattedStack = removeSpacesBraces(stack.toString());
        System.out.printf("%-21s", formattedStack); //to print temporary stack values
        tempGotoLookup += "[ "+ Integer.parseInt((stack.peek()).substring((stack.peek()).length()-1)) + "," + LHS + "]";
        System.out.printf("%-12s %-8s", tempGotoLookup, goToValue); //to print goto lookup & goto value
        stack.push(LHS.concat(goToValue)); //push items onto stack
        tempStackAction = "push "+LHS.concat(goToValue);
        System.out.printf("%-10s",tempStackAction); //to print stack action
        printParseTreeStack(actionValue, tempActionLookup, LHS);
        formattedParseTreeStack = getReverseStack(parseTreeStack);
        System.out.printf("%-50s\n", formattedParseTreeStack);
        //Clear temporary variables 
        LHS = "";
        lengthRHS = 0;
        tempActionLookup = "";
        tempGotoLookup = "";
        tempStackAction = "";
    }
    
    //Function to perform action lookup
    public static void actionLookup(int shiftSm, String shiftAi){
        actionValue = action[shiftSm][decodeActionIndices(shiftAi)];        
    }
    
    //Function to perform goto lookup
    public static void goToLookup(int reduceSm, String reduceAi){
        goToValue = goTo[reduceSm][decodeGoToIndices(reduceAi)];
    }
    
    //Function to deocode indices for action table 
    public static int decodeActionIndices(String actionIndex){
        int intActionIndex = 0;
        /*switch(actionIndex){
        case "id":  intActionIndex = 0;
                    break;
        case "+":   intActionIndex = 1;
                    break;
        case "*":   intActionIndex = 2;
                    break;
        case "(":   intActionIndex = 3;
                    break;
        case ")":   intActionIndex = 4;
                    break;
        case "$":   intActionIndex = 5;
                    break;
        default: break;
        }*/
	if (actionIndex.equalsIgnoreCase("id"))
		intActionIndex = 0;
	else if (actionIndex.equalsIgnoreCase("+"))
		intActionIndex = 1;
	else if (actionIndex.equalsIgnoreCase("*"))
		intActionIndex = 2;
	else if (actionIndex.equalsIgnoreCase("("))
		intActionIndex = 3;
	else if (actionIndex.equalsIgnoreCase(")"))
		intActionIndex = 4;
	else if (actionIndex.equalsIgnoreCase("$"))
		intActionIndex = 5;
        return intActionIndex;
    }
    
    //Function to deocode indices for goto table
    public static int decodeGoToIndices(String goToIndex){
        int intgoToIndex = 0;
        /*switch(goToIndex){            
        case "E":   intgoToIndex = 0;
                    break;
        case "T":   intgoToIndex = 1;
                    break;
        case "F":   intgoToIndex = 2;
                    break;
        default: break;
        }*/

	if (goToIndex.equalsIgnoreCase("E"))
		intgoToIndex = 0;
	else if (goToIndex.equalsIgnoreCase("T"))
		intgoToIndex = 1;
	else if (goToIndex.equalsIgnoreCase("F"))
		intgoToIndex = 2;
        return intgoToIndex;
    }
    
    public static String removeSpacesBraces (String inputToFormat){
        String formattedValue = new String();
        
        formattedValue = inputToFormat.replaceAll(",","");
        formattedValue = formattedValue.substring(1);
        formattedValue = formattedValue.replaceAll("]","");
        formattedValue = formattedValue.replaceAll(" ","");
        return formattedValue;
    }
    
    public static String getReverseStack (Stack<String> inputToFormat){
        String formattedValue = "";
        for (int i=0; i<inputToFormat.size(); i++){
            formattedValue = inputToFormat.get(i) + " " + formattedValue ;
        }
        return formattedValue;
    }
     
        public static void printParseTree(){
        String printId = new String();
        boolean idFlag = false, opNextFlag = false;
        int countOperator = 0, nextOperator = 0, countChar = 0, tempCountOperator = 0, tempCountChar = 0, idSpaces = 0;        
        for (int i=0; i < formattedParseTreeStack.length(); i++){
            if(idFlag){
                idFlag = false;
                continue;
            }
            else if (formattedParseTreeStack.charAt(i) == '[' || formattedParseTreeStack.charAt(i) == ']' || formattedParseTreeStack.charAt(i) == ' '){
                //i++;
                continue;
            }
            else if (formattedParseTreeStack.charAt(i) == 'i'){
                idFlag = true;
                printId = Character.toString(formattedParseTreeStack.charAt(i))+Character.toString(formattedParseTreeStack.charAt(i+1));
                idSpaces = countChar;
                System.out.println();
                while (idSpaces>0){
                    System.out.print(" ");
                    idSpaces--;
                }
                System.out.print("  "+printId);
            }
            else if (formattedParseTreeStack.charAt(i) == '+' || formattedParseTreeStack.charAt(i) == '*' 
                    || formattedParseTreeStack.charAt(i) == '(' || formattedParseTreeStack.charAt(i) == ')'){
                opNextFlag = true;
                printId = Character.toString(formattedParseTreeStack.charAt(i));
                countOperator = countOperator + 2;
                tempCountOperator = countOperator;
                System.out.println();
                while(tempCountOperator>0){
                    System.out.print(" ");
                    tempCountOperator--;
                }
                System.out.print(" "+printId);                
            }
            else{
                printId = Character.toString(formattedParseTreeStack.charAt(i));
                if(opNextFlag){
                    nextOperator = countOperator + 1;
                    System.out.println();
                    while (nextOperator>0){
                        System.out.print(" ");
                        nextOperator--;
                    }
                    System.out.print(""+printId);
                    opNextFlag = false; 
                    countChar = countChar - countOperator;
                }
                else {
                    if(i==1){
                        System.out.print(printId);
                        countChar++;
                    }
                    else{
                        if ((countChar == 5 || countChar == 6 || countChar == 7) && countOperator == 2)
                            tempCountChar = countChar-2;
                        else 
                            tempCountChar = countChar;
                        System.out.println();
                        while(tempCountChar > 0){
                            System.out.print(" ");
                            tempCountChar--;
                        }
                        System.out.print("  "+printId);
                        countChar = countChar + 2;
                    }
                }
                
            }
            
        }
        System.out.printf("\n");
	System.out.println("$");
    } 

public static void printParseTreeStack (String parseActionValue, String parseActionLookup, String parseValue){
       
        if (parseActionValue.contains("S")){
            if (parseActionLookup.contains("id"))
                parseTreeStack.push("id");
            //else if (parseActionLookup.contains("("))
              //  parseTreeStack.push("(");
            //else if (parseActionLookup.contains(")"))
                //parseTreeStack.push(")");
        }
        
        else if(parseActionValue.contains("R")){
            if(parseActionValue.equalsIgnoreCase("R1") || parseActionValue.equalsIgnoreCase("R3")){
                tempParseStack.add(parseTreeStack.pop());
                tempParseStack.add(parseTreeStack.pop());
                if(parseActionValue.equalsIgnoreCase("R1")){
                    tempParseTreeString = "[" + parseValue + " " + tempParseStack.pop() + " + " + tempParseStack.pop() + " ]";
                    parseTreeStack.push(tempParseTreeString);
                }
                else if(parseActionValue.equalsIgnoreCase("R3")){
                    tempParseTreeString = "[" + parseValue + " " + tempParseStack.pop() + " * " + tempParseStack.pop() + " ]";
                    parseTreeStack.push(tempParseTreeString);
                }
                //else if(parseActionValue.equalsIgnoreCase("R5")){
                    //tempParseTreeString = "[" + parseValue + " ( " + tempParseStack.pop() + " " + tempParseStack.pop() + " ) " + " ]";
                    //parseTreeStack.push(tempParseTreeString);
                //}
            }
            else if ( parseActionValue.equalsIgnoreCase("R5")){
                tempParseStack.add(parseTreeStack.pop());
                tempParseTreeString = "[" + parseValue + " ( " + tempParseStack.pop() + " )" + " ]";
                parseTreeStack.push(tempParseTreeString);
            }
                
                    
            else {
                tempParseTreeString = "[" + parseValue + " " + parseTreeStack.pop() + " ]";
                parseTreeStack.push(tempParseTreeString);
            }
        }     
    }
}
