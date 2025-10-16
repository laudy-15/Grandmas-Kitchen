# Grandmas-Kitchen
CSC 404 BYOL Project - Savannah and Emily

**Name:** Grandma’s Kitchen

**File Extension:**   .bake 

Description: Grandma’s Kitchen is a general purpose programming language where baking terms are the language. The syntax is inspired by Grandma’s recipe cards, allowing the users to code with a pinch of salt and a cup of humor. Grandma’s Kitchen supports basic control flow (while/for-loops), user-defined functions, and simple data structures. The data structures have unique and applicable names to the kitchen! An array is a “pantry”, and an arraylist is a “mixing bowl”. 


## Grammar
```
program         → recipeLine* EOF ;

recipeLine      → comment
             	   | varDecl
             	   | statement ;

comment         → "~" .* "~" ;

varDecl         → "grab" IDENTIFIER (“->“ expression)? "." ;  

expression     → assignment ;
assignment     → IDENTIFIER "->" assignment ;

statement       → actionStmt
              	  | loopStmt
              	  | ifStmt
              	  | printStmt 
		  | returnStmt
		  | block;

loopStmt        → "while" IDENTIFIER expression statement ;

ifStmt → "if" "(" expression ")" statement
               ( "else" statement )? ;

printtStmt      → "plate" expression “.” ;
actionStmt      → action IDENTIFIER;
action          → "beat"
        	        | "mix"
          	        | "bake"
                    | "chop"
                    | "fold"
                    | "whisk"
                    | "stir" ;
returnStmt → "serve" expression? "." ;

block → "{" recipeLine* "}" ;
```

## Sample Programs
### Program 1: "Hello, world"
```
plate “cookies”.
```

### Program 2: While Loop
```
add 5 eggs to mixing bowl        
while mixing bowl contains eggs
	beat egg ~beat is some operation~
	plate egg 
	remove egg from mixing bowl
```