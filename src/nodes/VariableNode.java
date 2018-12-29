package nodes;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang4.*;

public class VariableNode extends Node {

    String name;
    Value v = null;

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME
    ));

    public VariableNode(String name) {
        type = NodeType.VARIABLE;
        this.name = name;
    }

    public VariableNode(String name, Value v) {
        type = NodeType.VARIABLE;
        this.name = name;
        this.v = v;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public boolean parse() {
        return false;
    }

    public String toString() {
        return "Var：" + name;
    }

    //getterとsetter
    public void setValue(Value newv) {
        v = newv;
    } 

    public Value getValue() {
        return v;
    }
}
