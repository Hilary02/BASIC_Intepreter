package nodes;

import newlang5.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConstNode extends Node {

    private Value value;

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL,
            LexicalType.SUB //負数
    ));

    private ConstNode(Value v) {
        value = v;
        switch (v.getType()) {
            case INTEGER:
                type = NodeType.INT_CONSTANT;
                break;
            case DOUBLE:
                type = NodeType.DOUBLE_CONSTANT;
                break;
            case STRING:
                type = NodeType.STRING_CONSTANT;
                break;
            default:
                break;
        }
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Value v) {
        return new ConstNode(v);
    }

    @Override
    public boolean parse() throws Exception {
        return false;
    }

    @Override
    public Value getValue() throws Exception {
        return value;
    }

    public String toString() {
        return "" + value;
    }
}
