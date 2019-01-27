package nodes;

import newlang5.*;
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
            throw new Exception("ENDnodeが適切なタイミングで呼ばれませんでした");
        }
    }

    @Override
    public Value getValue() throws Exception {
        System.out.println("\nProgram END");
        return null;
    }

    @Override
    public String toString() {
        return "END";
    }
}
