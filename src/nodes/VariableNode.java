package nodes;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang5.*;

public class VariableNode extends Node {

    String var_name;
    Value v = null;

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME
    ));

    public VariableNode(String name) {
        type = NodeType.VARIABLE;
        var_name = name;
    }

    public VariableNode(LexicalUnit lu) {
        type = NodeType.VARIABLE;
        var_name = lu.getValue().getSValue();
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    /*
    public static Node getHandler(Environment env, LexicalUnit first) {
        if (isMatch(first.getType())) {
            VariableNode varNode;
            try {
                LexicalUnit lu = env.getInput().peep();
                String s = lu.getValue().getSValue();
                varNode = env.getVariable(s);
                return varNode;
            } catch (Exception e) {
            }
        }
        return null;
    }
     */
    public boolean parse() {
        return false;
    }

    public String toString() {
        return "V:" + var_name;
    }

    //getterとsetter
    public void setValue(Value newv) {
        this.v = newv;
    }

    public Value getValue() throws Exception {
        if (v == null) {
            throw new Exception("初期化されていない変数を参照しました");
        }
        return v;
    }
}
