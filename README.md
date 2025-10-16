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

printtStmt      → "put" expression “.” ;
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
put “cookies”.
```

### Program 2: While Loop
```
grab "egg1". ~var declaration~
grab "egg2".
grab "egg3".
grab "egg4".
grab "egg5".
grab bowl. ~list~

bowl -> {egg1, egg2, egg3, egg4, egg5}.

while 
    grab "curEgg" -> remove egg from bowl
	beat curEgg ~some operation~
	put curEgg ~print~
```
