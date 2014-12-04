package edu.louisiana.cacs.csce450Project;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author VenkataPradeep
 */
public class Scanner {
    /* Global Variables*/
    static int charClass;
    static String lexeme = new String();
    static char nextChar;
    static int lexLen;
    static int token;
    static int nextToken;
    
    static FileReader inputStream = null;
    
    /*Character Classes*/
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int UNKNOWN = 99;
    static final int EOF = -1;
    
    /*Token Codes*/
    static final int INT_LIT = 10;
    static final int IDENT = 11;
    static final int ASSIGN_OP = 20;
    static final int ADD_OP = 21;
    static final int SUB_OP = 22;
    static final int MULT_OP = 23;
    static final int DIV_OP = 24;
    static final int LEFT_PAREN = 25;
    static final int RIGHT_PAREN = 26;
    
    LinkedList<String> inputList = new LinkedList<String>();
    
    public Scanner(String inputFile){
        try {
            inputStream = new FileReader(inputFile);
            getChar();
            do {
                lex();
            } while(nextToken != EOF);                    
        } catch (FileNotFoundException e) {
            System.out.println("File open error.");
            System.out.println("Directory: "+ System.getProperty("user.dir"));
        } finally {
            if (inputStream != null) {
                try{
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("File close error.");
                }
            }
        }
    }
    
       /*Functions Used to do parsing*/
    
    /* getChar() - a function to get nextchar of the input and determine its character class*/
    void getChar(){
        
        int intInput = 0;
        try{
            intInput = inputStream.read();
            nextChar = (char) intInput;
           
        } catch (IOException e) {
            System.out.println("Input file read error.");
        }
        //if (Character.toString(nextChar) != null) {
        if (intInput != -1) {
            if(Character.isAlphabetic(nextChar)) 
                charClass = LETTER;
            else if (Character.isDigit(nextChar))
                charClass = DIGIT;
            else    
                charClass = UNKNOWN;
        }
        else
            charClass = EOF;
    }
    
    /* addChar() - Function to add nextchar to lexeme*/
    void addChar() {
        
        if (lexLen <= 98) {
            lexeme = lexeme.concat(Character.toString(nextChar));           
            nextChar++;
        }
        else
            System.out.println("Error - lexeme is too long \n");
    }
    
    void getNonBlank(){

        while(Character.isWhitespace(nextChar))
        getChar();
    }


    int lookup(char ch){

        switch(ch){
            case '(':
                addChar();
                nextToken = LEFT_PAREN;
                break;
            case ')':
                 addChar();
                nextToken = RIGHT_PAREN;
                break; 
            case '+':
                 addChar();
                nextToken = ADD_OP;
                break;
            case '-':
                 addChar();
                nextToken = SUB_OP;
                break;
            case '*':
                 addChar();
                nextToken = MULT_OP;
                break;
            case '/':
                 addChar();
                nextToken = DIV_OP;
                break;
            default:
                 addChar();
                nextToken = EOF;
                break;
        }
        return nextToken;
    }
    
    public int lex(){
        lexLen = 0;
        getNonBlank();
        
        switch (charClass){
            case LETTER:
                addChar();
                getChar();
                while(charClass == LETTER || charClass == DIGIT){
                    addChar();
                    getChar();
                }
                nextToken = IDENT;
                break;
            case DIGIT:
                addChar();
                getChar();
                while(charClass == DIGIT){
                    addChar();
                    getChar();
                }
                nextToken = INT_LIT;
                break;
            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;
            case EOF:
                nextToken = EOF;
                lexeme = "EOF";
        }
        inputList.add(lexeme);
        //System.out.println("Next token is: " + nextToken + ", Next lexeme is " + lexeme);
        lexeme = "";
        return nextToken;     
    }
}
