package kitchen;

import java.util.List;

interface KitchenCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
