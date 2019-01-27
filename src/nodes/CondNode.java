package nodes;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import newlang5.*;

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

    @Override
    public Value getValue() throws Exception {
        Value val1 = left.getValue();
        Value val2 = right.getValue();

        if (val1 == null || val2 == null) {
            throw new Exception("比較文にnullが含まれています");
        }

        //数値比較
        if ((val1.getType() == ValueType.INTEGER || val1.getType() == ValueType.DOUBLE)
                && (val2.getType() == ValueType.INTEGER || val2.getType() == ValueType.DOUBLE)) {
            if (operator == LexicalType.LT) {
                return new ValueImpl(val1.getDValue() < val2.getDValue());
            } else if (operator == LexicalType.LE) {
                return new ValueImpl(val1.getDValue() <= val2.getDValue());
            } else if (operator == LexicalType.GT) {
                return new ValueImpl(val1.getDValue() > val2.getDValue());
            } else if (operator == LexicalType.GE) {
                return new ValueImpl(val1.getDValue() >= val2.getDValue());
            } else if (operator == LexicalType.EQ) {
                return new ValueImpl(val1.getDValue() == val2.getDValue());
            } else if (operator == LexicalType.NE) {
                return new ValueImpl(val1.getDValue() != val2.getDValue());
            } else {
                throw new Exception("不正な演算子で数値の比較を試みました。");
            }
        }

        //文字列比較
        if (val1.getType() == ValueType.STRING && val2.getType() == ValueType.STRING) {
            if (operator == LexicalType.EQ) {
                return new ValueImpl(val1.getSValue().equals(val2.getSValue()));
            } else if (operator == LexicalType.NE) {
                return new ValueImpl(!val1.getSValue().equals(val2.getSValue()));
            } else {
                throw new Exception("文字列比較に使用できない演算子です。");
            }
        }

        //真偽値比較 使われない可能性が高そう？
        if (val1.getType() == ValueType.BOOL && val2.getType() == ValueType.BOOL) {
            if (operator == LexicalType.EQ) {
                return new ValueImpl(val1.getBValue() == val2.getBValue());
            } else if (operator == LexicalType.NE) {
                return new ValueImpl(val1.getBValue() != val2.getBValue());
            } else {
                throw new Exception("真偽値比較に使用できない演算子です。");
            }
        }
        //throw new Exception("比較できない組み合わせ、演算子です");
        return new ValueImpl(false);
    }

    public String toString() {
        return "(" + left + " " + OPERATOR_SET.get(operator) + " " + right + ")";
    }
}
