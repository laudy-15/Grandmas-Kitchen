package kitchen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kitchen.Expr.*;
import kitchen.Stmt.*;

public class Interpreter implements Stmt.Visitor<Void>, Expr.Visitor<Object> {
    final Environment globals = new Environment();
    private Environment environment = globals;
   /////// private final Map<String, Object> vars = new HashMap<>();
        // TODO: questions about pre-defining/checking ingredients & containers
        //.      are they both treated the same as in being stored here in `vars`
        

    

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Kitchen.runtimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    /* EXPRESSIONS: */

    @Override
    public Object visitIngredientExpr(Ingredient expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIngredientExpr'");
    }

    @Override
    public Object visitContainerExpr(Container expr) {
        String name = expr.tok.lexeme;  // either an ingredient or a container

        if (!environment.contains(name) && !globals.contains(name)) {
            environment.define(name, 0);
        } else if (environment.contains(name)) {
            return environment.getAt(0, name);
        } else {
            return globals.getAt(0, name);
        }
        return globals.getAt(0, name);      // shouldn't reach here
    }

    @Override
    public Object visitTopExpr(Top expr) {
        // return the first thing in the container (which should be an arraylist when looked up)
        String name = ((Container)expr.cont).tok.lexeme;
        if (!environment.contains(name)) {
            throw new RuntimeError(((Container)expr.cont).tok, "No such container '" + name + "'.");
        }
        Object value = environment.get(name);
        if (!(value instanceof ArrayList)) {
            throw new RuntimeError(((Container)expr.cont).tok, "'" + name + "' is not a container.");
        }
        ArrayList<Object> list = (ArrayList<Object>)value;
        if (list.isEmpty()) {
            throw new RuntimeError(((Container)expr.cont).tok, "'" + name + "' is empty.");
        }
        return list.get(0);
    }

    @Override
    public Object visitRestExpr(Rest expr) {
        // return the rest of the container (which should be an arraylist when looked up)
        String name = ((Container)expr.cont).tok.lexeme;
        if (!environment.contains(name)) {
            throw new RuntimeError(((Container)expr.cont).tok, "No such container '" + name + "'.");
        }
        Object value = environment.get(name);
        if (!(value instanceof ArrayList)) {
            throw new RuntimeError(((Container)expr.cont).tok, "'" + name + "' is not a container.");
        }
        ArrayList<Object> list = (ArrayList<Object>)value;
        if (list.isEmpty()) {
            throw new RuntimeError(((Container)expr.cont).tok, "'" + name + "' is empty.");
        }

        //list.remove(0);
        // TODO: question: should "rest of X"  actually remove from X or
        //         should it make a copy of X with the first thing removed

        ArrayList<Object> listCopy = new ArrayList<>();
        listCopy.addAll(list);
        listCopy.remove(0);

        return listCopy;
    }

    @Override
    public Object visitEmptyExpr(Empty expr) {
        String name = ((Container)expr.cont).tok.lexeme;
        if (!environment.contains(name)) {
            throw new RuntimeError(((Container)expr.cont).tok, "No such container '" + name + "'.");
        }
        Object value = environment.get(name);
        if (!(value instanceof ArrayList)) {
            throw new RuntimeError(((Container)expr.cont).tok, "'" + name + "' is not a container.");
        }
        ArrayList<Object> list = (ArrayList<Object>)value;
        boolean isEmpty = list.isEmpty();
        if (expr.not) {
            return !isEmpty;
        } else {
            return isEmpty;
        } 
    }

    @Override
    public Object visitCallExpr(Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) { 
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof KitchenCallable)) {
            throw new RuntimeError(expr.paren,
                "Can only call functions and classes.");
        }

        KitchenCallable function = (KitchenCallable)callee;
        if (arguments.size() != function.arity()) {
            System.out.println("Size of arguments list: " + arguments.size());
            System.out.println("arity (num of params collected): " + function.arity());
            throw new RuntimeError(expr.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitQuantityExpr(Quantity expr) {
        return expr.amount;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }



    /* STATEMENTS */

    @Override
    public Void visitDefineStmt(Define stmt) {
        String name = stmt.keyword.lexeme;
        if (environment.contains(name)) {
            throw new RuntimeError(stmt.keyword, "You already have a  '" + name + "'.");
        }
        environment.define(name, new ArrayList<Object>());
        return null;
    }

    @Override
    public Void visitAssignStmt(Assign stmt) {
        String name = stmt.cont.lexeme;
        if (!environment.contains(name)) {
            throw new RuntimeError(stmt.cont, "No such container '" + name + "'.");
        }
        Object value = evaluate(stmt.object);
        if (!(environment.get(name) instanceof ArrayList)) {
            throw new RuntimeError(stmt.cont, "'" + name + "' is not a container.");
        }   
        ArrayList<Object> list = (ArrayList<Object>)environment.get(name);
        list.add(value);
        return null;
    }
    
    @Override
    public Void visitIfStmt(If stmt) {
        Object condition = evaluate(stmt.condition);
        if (!(condition instanceof Boolean)) {
            throw new RuntimeError(null, "Condition must be a boolean.");
        }
        if ((Boolean)condition) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        Object condition = evaluate(stmt.condition);
        if (!(condition instanceof Boolean)) {
            throw new RuntimeError(null, "Condition must be a boolean.");
        }
        while (!(Boolean)condition) {
            for (Stmt bodyStmt : stmt.body) {
                execute(bodyStmt);
            }
            condition = evaluate(stmt.condition);
            if (!(condition instanceof Boolean)) {
                throw new RuntimeError(null, "Condition must be a boolean.");
            }
        }
        return null;
    }

    @Override
    // Sums up the contents of a container and stores it back in the container
    public Void visitMixStmt(Mix stmt) {
        String name = stmt.cont.lexeme; // container name
        if (!environment.contains(name)) {
            throw new RuntimeError(stmt.cont, "No such container '" + name + "'.");
        }
        Object value = environment.get(name); // get the container values (should be an ArrayList)
        if (!(value instanceof ArrayList)) {
            throw new RuntimeError(stmt.cont, "'" + name + "' is not a container.");
        }
        ArrayList<Object> list = (ArrayList<Object>)value; //cast to arrayList after the check ^
        if (list.isEmpty()) {
            throw new RuntimeError(stmt.cont, "'" + name + "' is empty.");
        }
        double sum = 0;
        for (Object obj : list) {
            if (obj instanceof Double) {
                sum += (Double)obj;   // sum up the things in the array
            } else {
                throw new RuntimeError(stmt.cont, "Cannot mix non-numeric ingredient.");
            }
        }
        list.clear(); //clear the old values
        list.add(sum); // add the sum of the values back into the container
        return null;
    }

    @Override
    // removes the top of the given container
    public Void visitShakeStmt(Shake stmt) {
        String name = stmt.cont.lexeme; // container name
        if (!environment.contains(name)) {
            throw new RuntimeError(stmt.cont, "No such container '" + name + "'.");
        }
        Object value = environment.get(name); // get the container values (should be an ArrayList)
        if (!(value instanceof ArrayList)) {
            throw new RuntimeError(stmt.cont, "'" + name + "' is not a container.");
        }
        ArrayList<Object> list = (ArrayList<Object>)value; //cast to arrayList after the check ^
        if (list.isEmpty()) {
            throw new RuntimeError(stmt.cont, "'" + name + "' is empty.");
        }
        list.remove(0); // remove the top of the container
        return null;
    }

    @Override
    public Void visitRecipeStmt(Recipe stmt) {
        KitchenFunction function = new KitchenFunction(stmt, environment,
                                            false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    // note: not private
    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;    // restore
        }
    }

    @Override
    public Void visitServeStmt(Stmt.Serve stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Serve(value);
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.value);
        System.out.println(stringify(value));
        return null;
    }


    /* HELPER METHODS: */

    private String stringify(Object object) {
        return object.toString();
    }


}
