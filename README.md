# Grandmas-Kitchen
CSC 404 BYOL Project - Savannah and Emily

**Name:** Grandma’s Kitchen

**File Extension:**   .bake 

Description: Grandma’s Kitchen is a general purpose programming language where baking terms are the language. The syntax is inspired by Grandma’s recipe cards, allowing the users to code with a pinch of salt and a cup of humor. Grandma’s Kitchen supports basic control flow (while loops), user-defined functions, and array lists. The variables and lists have unique and applicable names to the kitchen! 


## Grammar
```
recipe -> recipe for <name> [using <ingredients>+]? ( [stmt]+ ).

stmt -> grab <container>.
      | [add|combine|pour] <expr> into <container>.
      | if <expr> then [stmt]+ [otherwise [stmt]+] and continue.
      | repeat [stmt]+ until <expr>.
      | plate <expr>.  ~~print statement
      | serve <expr>.  ~~return statement
      | <expr> with <expr>    ~~ part of function call
      | <name> [with <expr>+]?  ~~ function call
      | mix <containeer>.    ~~~ sum up everything in container into one number in that container
      | shake <container>. ~~removes the top of the container

expr -> <ingredient> 
       | <container>
       | top of <container>
       | rest of <container> 
       | <container> is [not] empty
       | <recipeName>([<expr>]).
       | <num> [cups|spoons|..]? [of]? <ingredient>
       | <Object>

```

## Sample Programs

### Printing:
```
plate "Hello world".
```
#### Output:
```
Hello world
```
___
### Variable Declaration:
```
grab bowl.
plate bowl is empty.
add 3 eggs into bowl.
add 4 cups of flour into bowl.
add 4 tablespoons of salt into bowl.
plate bowl.
```
#### Output:
```
[3.0, 4.0, 4.0]
```
___
### Boolean Statements:
```
plate bowl is not empty.
plate bowl is empty.
```
#### Output:
```
true
false
```
___
### If-Statements:
```
if bowl is empty then plate "hello". otherwise plate "goodbye". and continue.
```
#### Output:
```
goodbye
```
___
### Sum:
```
mix bowl.
plate bowl.
```
#### Output:
```
[11.0]
```
___
### While-Loop:
```
grab pyrex.
add 1 cup of flour into pyrex.
add 2 eggs into pyrex.
add 3 tablespoons of oil into pyrex.
add 4 teaspoons of vanilla into pyrex.
repeat
  plate top of pyrex.
  shake pyrex.
 until pyrex is empty.
plate pyrex.
```
#### Output:
```
1.0
2.0
3.0
4.0
[]
```
___
### Function to calculate Fibonacci numbers:
```
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
```
## How to run the Grandma's Kitchen Interpreter
Run the main method in the Kitchen.java class. The main supports file input and terminal input.