# Grandmas-Kitchen
CSC 404 BYOL Project - Savannah and Emily

**Name:** Grandma’s Kitchen

**File Extension:**   .bake 

Description: Grandma’s Kitchen is a general purpose programming language where baking terms are the language. The syntax is inspired by Grandma’s recipe cards, allowing the users to code with a pinch of salt and a cup of humor. Grandma’s Kitchen supports basic control flow (while/for-loops), user-defined functions, and simple data structures. The data structures have unique and applicable names to the kitchen! An array is a “pantry”, and an arraylist is a “mixing bowl”. 


## Grammar
```
recipe -> recipe for <name> [using <ingredients>+]? . [stmt]+

stmt -> grab <container>.
      | [add|combine|pour] <expr> into <container>.
      | if <expr> then [stmt]+ [otherwise [stmt]+] and continue.
      | repeat [stmt]+ until <expr>.
      | plate <expr>.  ~~print statement
      | serve <expr>.  ~~return statement
      | mix <containeer>.    ~~~ sum up everything in container into one number in that container
      | shake <container>. ~~removes the top of the container

expr -> <ingredient> 
       | <container>
       | top of <container>
       | rest of <container> 
       | <expr> with <expr>    ~~ part of function call
       | <container> is [not] empty
       | <name> [with <expr>+]?  ~~ function call
       | <num> [cups|spoons|..]? [of]? <ingredient>
       | <Object>

 var -> <ingredient> 
       | <container>  
```

## Sample Programs
```
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


recipe for fib using eggs:
   grab plate.
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
```

## Workspace:

containers  -> pot | bowl | pan | cup | saucer | sheet | ...
ingredients  -> eggs | flour | sugar | milk | ....

utensils ~~~~ lists
ingredients ~~~~~ constants


recipe -> recipe for <name> [using <ingredients>+]? . [stmt]+

stmt -> grab <container>.
      | [add|combine|pour] <expr> into <container>.
      | if <expr> then [stmt]+ [otherwise [stmt]+] and continue.
      | repeat [stmt]+ until <expr>.
      | plate <expr>.  ~~print statement
      | serve <expr>.  ~~return statement
      | mix <containeer>.    ~~~ sum up everything in container into one number in that container
      | shake <container>. ~~removes the top of the container

expr -> <ingredient> 
       | <container>
       | top of <container>
       | rest of <container> 
       | <expr> with <expr> ~~ also function call
       | <container> is [not] empty
       | <name> [with <expr>+]?  ~~ function call
       | <num> [cups|spoons|..]? [of]? <ingredient>  
       | <Object>

 var -> <ingredient> 
       | <container>       



code to run the test file:
  & 'C:\Program Files\Eclipse Adoptium\jdk-17.0.8.7-hotspot\bin\java.exe' '-XX:+ShowCodeDetailsInExceptionMessages' '-cp' 'C:\Users\savan\AppData\Roaming\Code\User\workspaceStorage\8ae14183fc085ce81238a7e752060038\redhat.java\jdt_ws\Grandmas-Kitchen_848ff9ef\bin' 'kitchen.Kitchen'  .\demo1.kit