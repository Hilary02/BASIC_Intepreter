package nodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import newlang4.Environment;
import newlang4.LexicalType;
import newlang4.LexicalUnit;
import newlang4.Value;

public class StmtNode extends Node {

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            addAll(SubstNode.FIRST_SET);
            addAll(FunctionCallNode.FIRST_SET);
            addAll(ForStmtNode.FIRST_SET);
            addAll(EndNode.FIRST_SET);
        }
    };

    private StmtNode(Environment env) {
        super(env);
        type = NodeType.STMT;
    }

    static boolean isMatch(LexicalType t) {
        return FIRST_SET.contains(t);
    }

    public static Node getHandler(Environment env) throws Exception {
        switch (env.getInput().peep().getType()) {
            case NAME:
                // <leftvar> <EQ>
                if (env.getInput().peep(2).getType() == LexicalType.EQ) {
                    return SubstNode.getHandler(env);
                }
                // <NAME> (<LP>) <expr_list>
                if (ExprListNode.isMatch(env.getInput().peep(2).getType())) {
                    return FunctionCallNode.getHandler(env);
                }
                throw new Exception("StmtNodeの解析中に構文エラーが起きました");
            case FOR:
                return ForStmtNode.getHandler(env);
            case END:
                return EndNode.getHandler(env);
            default:
                throw new Exception("Stmt文の開始として不適切な型です。:" + env.getInput().peep().getType());
        }
    }

    public Value getValue() throws Exception {
        return null;
    }

    public String toString() {
        return "This is a Stmt Node."; //呼ばれたら何かおかしい 
    }
}
