package Lexical;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

class Token {
    private TokenType type;
    private String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }
}

enum TokenType {
    KEYWORD, IDENTIFIER, OPERATOR, NUMBER, STRING, SYMBOL
}

public class LexicalAnalyzer {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final String OPERATORS = "+-*/()=";
    private static final List<String> KEYWORDS = Arrays.asList("int", "float", "double", "String"); // Added "String"
    private static final String SYMBOL = ";";
    private static final String STRING_DELIMITER = "'";

    public static List<Token> analyze(String sourceCode) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean insideString = false;
        char stringDelimiter = '\0';

        for (char c : sourceCode.toCharArray()) {
            if (Character.isWhitespace(c) && !insideString) {
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken = new StringBuilder();
                }
            } else if (c == '"' || c == '\'') {
                if (insideString && c == stringDelimiter) {
                    currentToken.append(c);
                    tokens.add(createToken(currentToken.toString()));
                    currentToken = new StringBuilder();
                    insideString = false;
                } else if (!insideString) {
                    insideString = true;
                    stringDelimiter = c;
                    currentToken.append(c);
                }
            } else if (OPERATORS.contains(String.valueOf(c)) && !insideString) {
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken = new StringBuilder();
                }
                tokens.add(createToken(String.valueOf(c)));
            } else if (c == ';') {
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                }
                tokens.add(createToken(String.valueOf(c)));
                currentToken = new StringBuilder();
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(createToken(currentToken.toString()));
        }

        return tokens;
    }

    private static Token createToken(String lexeme) {
        if (isNumber(lexeme)) {
            return new Token(TokenType.NUMBER, lexeme);
        }
        if (OPERATORS.contains(lexeme) && !lexeme.equals("\"") && !lexeme.equals("'")) {
            return new Token(TokenType.OPERATOR, lexeme);
        }
        if (KEYWORDS.contains(lexeme)) {
            return new Token(TokenType.KEYWORD, lexeme);
        }
        if (SYMBOL.equals(lexeme)){
            return new Token(TokenType.SYMBOL, lexeme);
        }
        if (lexeme.startsWith(STRING_DELIMITER) && lexeme.endsWith(STRING_DELIMITER)) {
            return new Token(TokenType.STRING, lexeme);
        }
        return new Token(TokenType.IDENTIFIER, lexeme);
    }

    private static boolean isNumber(String lexeme) {
        Matcher matcher = NUMBER_PATTERN.matcher(lexeme);
        return matcher.matches();
    }

    public static void main(String[] args) {
        String sourceCode = "int x = 42;";
        List<Token> tokens = analyze(sourceCode);

        for (Token token : tokens) {
            System.out.println("Type: " + token.getType() + ", Lexeme: " + token.getLexeme());
        }

        try {
            FileWriter writer = new FileWriter("output.txt");

            for (Token token : tokens) {
                writer.write("Type: " + token.getType() + ", Lexeme: " + token.getLexeme() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
