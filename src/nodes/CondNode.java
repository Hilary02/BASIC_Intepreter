package nodes;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import newlang4.*;

public class CondNode extends Node {

    private Node left;
    private Node right;
    private LexicalType operator;

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            addAll(ExprNode.FIRST_SET);
        }
    };

    private final static Map<LexicalType, String> OPERATOR_SET = new HashMap<>();

    static {
        OPERATOR_SET.put(LexicalType.EQ, "=");
        OPERATOR_SET.put(LexicalType.GT, ">");
        OPERATOR_SET.put(LexicalType.LT, "<");
        OPERATOR_SET.put(LexicalType.GE, ">=");
        OPERATOR_SET.put(LexicalType.LE, "<=");
        OPERATOR_SET.put(LexicalType.NE, "!=");
    }

    private CondNode(Environment env) {
        super(env);
        type = NodeType.COND;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new CondNode(env);
    }

    public boolean parse() throws Exception {
        if (ExprNode.isMatch(env.getInput().peep().getType())) {
            left = ExprNode.getHandler(env);
            try {
                left.parse();
            } catch (Exception e) {
                throw new Exception("条件文の左辺の解析中にエラーが起きました。\n" + e);
            }
        } else {
            throw new Exception("条件文の左辺の開始が不適切です。");
        }

        if (OPERATOR_SET.containsKey(env.getInput().peep().getType())) {
            operator = env.getInput().get().getType();
        } else {
            throw new Exception("条件文に不適切な記号です。:" + env.getInput().peep().getType());
        }

        if (ExprNode.isMatch(env.getInput().peep().getType())) {
            right = ExprNode.getHandler(env);
            try {
                right.parse();
            } catch (Exception e) {
                throw new Exception("条件文の右辺の解析中にエラーが起きました。\n" + e);
            }
        } else {
            throw new Exception("条件文の右辺が不適切な開始です。");
        }
        return true;
    }

//    public Value getValue() throws Exception {
//        Value val1 = left.getValue();
//        Value val2 = right.getValue();
//        if (val1 == null || val2 == null) {
//            throw new CalcurateException("nullに対して演算を試みました。");
//        }
//        if (val1.getType() == ValueType.STRING || val2.getType() == ValueType.STRING) {
//            if (operator == LexicalType.EQ) {
//                return new ValueImpl(val1.getSValue().equals(val2.getSValue()));
//            } else if (operator == LexicalType.NE) {
//                return new ValueImpl(!val1.getSValue().equals(val2.getSValue()));
//            } else {
//                throw new CalcurateException("文字列に対して無効な演算子が指定されています。");
//            }
//        }
//
//        if (operator == LexicalType.LT) {
//            return new ValueImpl(val1.getDValue() < val2.getDValue());
//        } else if (operator == LexicalType.LE) {
//            return new ValueImpl(val1.getDValue() <= val2.getDValue());
//        } else if (operator == LexicalType.GT) {
//            return new ValueImpl(val1.getDValue() > val2.getDValue());
//        } else if (operator == LexicalType.GE) {
//            return new ValueImpl(val1.getDValue() >= val2.getDValue());
//        } else if (operator == LexicalType.EQ) {
//            return new ValueImpl(val1.getDValue() == val2.getDValue());
//        } else if (operator == LexicalType.NE) {
//            return new ValueImpl(val1.getDValue() != val2.getDValue());
//        } else {
//            throw new InternalError("不正な演算子で条件判断を試みました。");
//        }
//    }
    public String toString() {
        return "(" + left + " " + OPERATOR_SET.get(operator) + " " + right + ")";
    }
}
