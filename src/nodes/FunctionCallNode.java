package nodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import newlang4.Environment;
import newlang4.LexicalType;
import newlang4.LexicalUnit;

// <call_sub> ::=
//      <NAME> <expr_list>
// <call_func> ::=
//      <NAME> <LP> <expr_list> <RP>
public class FunctionCallNode extends Node {

    String funcName;
    Node arguments;
    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(LexicalType.NAME));

    private FunctionCallNode(Environment env) {
        super(env);
        type = NodeType.FUNCTION_CALL;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new FunctionCallNode(env);
    }

    public boolean parse() throws Exception {
        boolean useBracket = false;
        funcName = env.getInput().get().getValue().getSValue();

        //`(つきパターン`
        if (env.getInput().expect(LexicalType.LP)) {
            useBracket = true;
            env.getInput().get();
        }
        if (ExprListNode.isMatch(env.getInput().peep().getType())) {
            arguments = ExprListNode.getHandler(env);
            arguments.parse();
        }
        //)が正しくあるかどうか
        if (useBracket && !env.getInput().expect(LexicalType.RP)) {
            throw new Exception("対応する括弧が閉じられていません");
        }
        if (useBracket && env.getInput().expect(LexicalType.RP)) {
            env.getInput().get();
        }
        return true;
    }

    public String toString() {
        return "func: " + funcName + "(" + arguments + ")";
    }
}
