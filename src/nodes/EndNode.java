package nodes;

import newlang4.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EndNode extends Node {

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(LexicalType.END));

    private EndNode(Environment env) {
        super(env);
        type = NodeType.END;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new EndNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        if (env.getInput().peep().getType() == LexicalType.END) {
            env.getInput().get();
            return true;
        } else {
            throw new Exception("\nSyntax Error : When EndNode parsing.");
        }
    }

    @Override
    public String toString() {
        return "END";
    }
}
