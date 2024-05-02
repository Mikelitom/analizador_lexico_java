import java.util.Stack;

public class Analizer {
  Stack<Token> tokenStack = new Stack<Token>();

  String lexema = "";
  int state = 0;
  int column;
  char character;
  int matrixValue;
  int i;

  char[] entry;

  public void analizerMessage(String message) {
    System.out.println("---------------------------------");
    System.out.println(" Analizer -> " + message);
    System.out.println("---------------------------------");
  }

  public int[][] transitionMatrix = {
          //    _    c    d    +    -    *    /    <    >    |    .    ,    ;    "    '    =    oc  EOF  EOL   EB  TAB    
          //    0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20    
    /* 0  */ {   1,   1,   2, 103, 104,   5,   7,  11,  12, 117, 118, 119, 120,  14, 121,  13,   0,   0,   0,   0,   0 },
    /* 1  */ {   1,   1,   1, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 },
    /* 2  */ { 501, 501,   2, 101, 101, 101, 101, 101, 101, 101,   3, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101 },
    /* 3  */ { 501, 501,   4, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501 },
    /* 4  */ { 501,	501,	 4,	102, 102,	102, 102,	102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102 },
    /* 5  */ { 105,	105, 105,	105, 105,	  6, 105,	105, 105,	105, 105, 105, 105, 105, 105, 105, 105, 105, 105, 105, 105 },
    /* 6  */ { 107,	107, 107,	107, 107,	107, 107,	107, 107,	107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107 },
    /* 7  */ { 106,	106, 106,	106, 106,	  9,	 8,	106, 106,	106, 106, 106, 106, 106, 106, 113, 106, 106, 106, 106, 106 },
    /* 8  */ {   8,	  8, 	 8,	  8,	 8,	  8,	 8,	  8,	 8,	  8,   8,	  8,	 8,	  8,	 8,	  8,	 8,	  8,	 0,	  8,	 8 },
    /* 9  */ {   9,	  9,	 9,	  9,	 9,	 10,	 9,	  9,	 9,	  9,   9,	  9,	 9,	  9,	 9,	  9,	 9, 503,	 9,	  9,	 9 },
    /* 10 */ {   9,	  9,	 9,	  9,	 9,	  9,	 0,	  9,	 9, 	9,   9,   9,	 9,	  9,	 9, 	9,	 9, 503,	 9,	  9,	 9 },
    /* 11 */ { 108,	108, 108,	108, 108,	108, 108,	108, 108,	108, 108, 108, 108, 108, 108, 109, 108, 108, 108, 108, 108 },
    /* 12 */ { 110, 110, 110, 110, 110, 110, 110, 110, 110, 110, 110, 110, 110, 110, 110, 111, 110, 110, 110, 110, 110 },
    /* 13 */ { 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 112, 123, 123, 123, 123, 123 },
    /* 14 */ {  14,  14,	14,	 14,	14,	 14,	14,	 14,	14,	 14,  14,	 14,	14, 124,	14,	 14,	14, 504, 504,  14,	14 }
  };

  public String[][] reservedWordsMatrix = {
    //           0          1  
    /*  0 */ { "200", "program"   },
    /*  1 */ { "201", "character" },
    /*  2 */ { "202", "integer"   },
    /*  3 */ { "203", "real"      },
    /*  4 */ { "204", "boolean"   },
    /*  5 */ { "205", "begin"     },
    /*  6 */ { "206", "end"       },
    /*  7 */ { "207", "read"      },
    /*  8 */ { "208", "write"     }, 
    /*  9 */ { "209", "if"        },
    /* 10 */ { "210", "then"      },
    /* 11 */ { "211", "else"      }, 
    /* 12 */ { "212", "while"     },
    /* 13 */ { "213", "do"        },
    /* 14 */ { "214", "or"        },
    /* 15 */ { "216", "not"       },
    /* 16 */ { "217", "implicit"  },
    /* 17 */ { "218", "none"      },
    /* 18 */ { "219", "dimension" },
    /* 19 */ { "220", "print"     }
  };

  public String[][] errorMatrix = {
    //           0                 1  
    /*  0 */ { "500", "Se esperaba un caracter"  },
    /*  1 */ { "501", "Se esperaba un digito"    },
    /*  2 */ { "502", "Caracter no identificado" },
    /*  3 */ { "503", "Comentario abierto"       },
    /*  4 */ { "504", "Cadena abierta"           }
  };

  public void run(String str) {
    entry = str.toCharArray();

    try {
      for (i = 0; i < entry.length; i++) {
        character = entry[i];

        if (character == ' ') {
          if (matrixValue == 14 || matrixValue == 9 || matrixValue == 10) {
            lexema += ' ';
            continue;
          } else if (!lexema.isEmpty()) {
            if (matrixValue >= 100) {
              Token token = new Token(matrixValue, lexema);
              tokenStack.push(token);
              matrixValue = 0;
              state = 0;
              lexema = "";
            } else {
              matrixValue = transitionMatrix[matrixValue][17];
              Token token = new Token(matrixValue, lexema);
              tokenStack.push(token);
              matrixValue = 0;
              state = 0;
              lexema = "";
            }

            while (character == ' ') {
              i++;
              character = entry[i];
            }
          }

          
        } 


        if (Character.isLetter(character)) {
          column = 1;
        } else if (Character.isDigit(character)) {
          column = 2;
        } else {
          switch (character) {
            case '_':
              column = 0;
              break;
            case '+':
              column = 3;
              break;
            case '-':
              column = 4;
              break;
            case '*':
              column = 5;
              break;
            case '/':
              column = 6;
              break;
            case '<':
              column = 7;
              break;
            case '>':
              column = 8;
              break;
            case '|':
              column = 9;
              break;
            case '.':
              column = 10;
              break;
            case ',':
              column = 11;
              break;
            case ';':
              column = 12;
              break;
            case '"':
              column = 13;
              break;
            case '\'':
              column = 14;
              break;
            case '=':
              column = 15;
              break;
            case ' ':
              column = 19;
              break;
            default:
              break;
          }
        }

        matrixValue = transitionMatrix[state][column];
        
        

        if (matrixValue < 100) {
          
          state = matrixValue;

          if (state == 0) {
            lexema = "";
          } else {
            lexema += character;
          }
        } else if (matrixValue >= 100 && matrixValue < 500) {

          if (matrixValue == 100) {
            System.out.println(matrixValue);
            validateReservedWord();
          }

          if (matrixValue == 100 || matrixValue == 101 || matrixValue == 102 || matrixValue == 105 ||
              matrixValue == 106 || matrixValue == 108 || matrixValue == 110 || 
              matrixValue == 123 || matrixValue >= 200) {
            i--;
          } else {
            lexema += character;
          }

          if (character != ' ') {
            Token token = new Token(matrixValue, lexema);
            tokenStack.push(token);
          }

          matrixValue = 0;
          state = 0;
          lexema = "";
        } else {
          errorMessage();
          break;
        }

        validateReservedWord();
      }

      if (!lexema.isEmpty()) {
        validateReservedWord();

        if (matrixValue >= 100 && matrixValue < 500) {
          
          Token token = new Token(matrixValue, lexema);
          tokenStack.push(token);
          matrixValue = 0;
          state = 0;
          lexema = "";
        } else if (matrixValue < 100) {
          matrixValue = transitionMatrix[matrixValue][17];

          if (matrixValue >= 500) {
            errorMessage();
          } else {
            Token token = new Token(matrixValue, lexema);
            tokenStack.push(token);
            matrixValue = 0;
            state = 0;
            lexema = "";
          }
          
        } else {
          
        }
      }
    } catch (Exception e) {
      analizerMessage(e.getMessage());
    }
  }

  public void validateReservedWord() {
    for (int i = 0; i < reservedWordsMatrix.length; i++) {
      if (lexema.equals(reservedWordsMatrix[i][1])) {
        matrixValue = Integer.parseInt(reservedWordsMatrix[i][0]); 
        break;
      }
    }
  }

  public void errorMessage() {
    if (character != -1 && matrixValue >= 500) {
      for (int i = 0; i < errorMatrix.length; i++) {
        if (matrixValue == Integer.parseInt(errorMatrix[i][0])) {
          analizerMessage("Error: " + errorMatrix[i][0] + " " + errorMatrix[i][1]);
        }
      }
    }
  }

  public void printTokens() {
    while (!tokenStack.isEmpty()) {
      Token token = tokenStack.pop();
      System.out.println(token.idToken + " -> " + token.lexema);
    }
  }
}
