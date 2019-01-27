package nodes;

import java.util.ArrayList;
import newlang5.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExprListNode extends Node {

    List<Node> child = new ArrayList<Node>(); //ジェネリクスも書くこと

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            addAll(ExprNode.FIRST_SET);
        }
    };

    private ExprListNode(Environment env) {
        super(env);
        type = NodeType.EXPR_LIST;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new ExprListNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        while (true) {
            if (ExprNode.isMatch(env.getInput().peep().getType())) {
                Node expr = ExprNode.getHandler(env);
                expr.parse();
                child.add(expr);
            } else {
                throw new Exception("引数リストに式として不適切な入力です");
            }
            //終端かどうか判定
            if (env.getInput().expect(LexicalType.COMMA)) {
                env.getInput().get();
                continue;   //まだ式が来る
            } else {
                break;
            }
        }

        return true;
    }

    @Override
    public Value getValue() throws Exception {
        throw new Exception("ExprListNodeからgetValueはできません");
        //return null;
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < child.size() - 1; i++) {
            str += child.get(i) + ",";
        }
        str += child.get(child.size() - 1);
        return str;
    }

    public List<Node> getChild() {
        return child;
    }
}
