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

repeat 5 times:
    grab "curEgg" -> remove egg from bowl
	beat curEgg ~some operation~
	put curEgg ~print~



grab pot.   ~~~~    pot = []
add eggs to pot.     ~~~~~ pot = cons eggs pot
combine eggs with pot into bowl.   ~~~~   bowl = cons eggs pot
pour pot into bowl.        ~~~~ bowl = pot

 ... top of pot ...     ~~~~~   first pot
.... rest of pot ....            ~~~~ rest pot

  bowl is empty  .... ~~~~~    empty? bowl
  bowl is not empty ....  

if bowl is empty 
then 
   add eggs to bowl.
   pour bowl into plate.
otherwise
   combine eggs with bowl into pan.
and continue.

repeat 
   add eggs to bowl
   pour bowl into plate
until weight of bowl > weight of plate.


for each <thing> in the bowl:
    add <thing> to pot
```


recipe for fib using eggs:
   if weight of eggs is 0 or weight of eggs is 1 then
    serve eggs
   otherwise
    grab plate.
    pour fib with rest of eggs into plate.
    combine fib with rest of rest of eggs into plate.
    serve plate.
   and continue.

grab pot.
add 3 cups of milk into pot.
serve fib with pot.




fib(N) = if n == 0 or 1 return n, else return fib(n-1) + fib(n-2)


recipe -> recipe for <name> [using <ingredients>+]? . [stmt]+

stmt -> grab <container>.
      | [add|combine|pour] <expr> into <container>.
      | if <expr> then [stmt]+ [otherwise [stmt]+] and continue.
      | repeat [stmt]+ until <expr>.
      | plate <expr>.
      | serve <expr>.                 ~~~~~~~ return statement

expr -> <ingredient> 
       | <container>
       | top of <container>
       | rest of <container> 
       | <expr> with <expr> 
       | <container> is [not] empty
       | <name> [with <expr>+]?  
       | <num> [cups|spoons|..] of <ingredient>               

containers  -> pot | bowl | plate | pan | cup | saucer | sheet | ...
ingredients  -> eggs | flour | sugar | milk | ....



utensils ~~~~ lists
ingredients ~~~~~ constants
