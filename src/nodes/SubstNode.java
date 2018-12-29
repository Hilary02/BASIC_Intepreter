package nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import newlang4.Environment;
import newlang4.LexicalType;
import newlang4.LexicalUnit;

public class SubstNode extends Node {

    String leftvar;
    Node expr;

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            add(LexicalType.NAME);
        }
    };

    private SubstNode(Environment env) {
        super(env);
        type = NodeType.ASSIGN_STMT;
    }

    static boolean isMatch(LexicalType t) {
        return FIRST_SET.contains(t);
    }

    public static SubstNode getHandler(Environment env) {
        return new SubstNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        if (env.getInput().expect(LexicalType.NAME)) {
            leftvar = env.getInput().get().getValue().getSValue();
        } else {
            throw new Exception("不適切な代入文です");
        }

        if (env.getInput().get().getType() != LexicalType.EQ) {
            throw new Exception("代入文に＝がありません。");
        }

        if (ExprNode.isMatch(env.getInput().peep().getType())) {
            expr = ExprNode.getHandler(env);
            expr.parse();
        } else {
            throw new Exception("代入文の後半が式として評価できません");
        }
        return true;
    }

    public String toString() {
        return String.format("[%s <- %s]", leftvar, expr);
    }
}
