package kitchen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kitchen.Expr.*;
import kitchen.Stmt.*;

public class Interpreter implements Stmt.Visitor<Void>, Expr.Visitor<Object> {
    private final Map<String, Object> vars = new HashMap<>();

    void dump() {
        System.out.println("=== Variable Dump ===");
        for (String var : vars.keySet()) {
            System.out.println(var + " : " + vars.get(var));
        }
    }

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

    @Override
    public Object visitIngredientExpr(Ingredient expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIngredientExpr'");
    }

    @Override
    public Object visitContainerExpr(Container expr) {
        String name = expr.tok.lexeme;  // either an ingredient or a container
        if (!vars.containsKey(name)) {
            vars.put(name, 0);
        }
        return vars.get(name);
    }

    @Override
    public Object visitEmptyExpr(Empty expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitEmptyExpr'");
    }

    @Override
    public Object visitQuantityExpr(Quantity expr) {
        return expr.amount;
    }

    
    @Override
    public Object visitTopExpr(Top expr) {
        // return the first thing in the container (which should be an arraylist when looked up)
        String name = ((Container)expr.cont).tok.lexeme;
        if (!vars.containsKey(name)) {
            throw new RuntimeError(((Container)expr.cont).tok, "No such container '" + name + "'.");
        }
        Object value = vars.get(name);
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
    public Void visitDefineStmt(Define stmt) {
        String name = stmt.keyword.lexeme;
        if (vars.containsKey(name)) {
            throw new RuntimeError(stmt.keyword, "You already have a  '" + name + "'.");
        }
        vars.put(name, new ArrayList<Object>());
        return null;
    }

    @Override
    public Void visitDeclareStmt(Declare stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDeclareStmt'");
    }

    @Override
    public Void visitIfStmt(If stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIfStmt'");
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitWhileStmt'");
    }

    @Override
    public Void visitAssignStmt(Assign stmt) {
        String name = stmt.cont.lexeme;
        if (!vars.containsKey(name)) {
            throw new RuntimeError(stmt.cont, "No such container '" + name + "'.");
        }
        Object value = evaluate(stmt.object);
        if (!(vars.get(name) instanceof ArrayList)) {
            throw new RuntimeError(stmt.cont, "'" + name + "' is not a container.");
        }   
        ArrayList<Object> list = (ArrayList<Object>)vars.get(name);
        list.add(value);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitReturnStmt'");
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.value);
        System.out.println(stringify(value));
        return null;
    }

    private String stringify(Object object) {
        return object.toString();
    }

}
