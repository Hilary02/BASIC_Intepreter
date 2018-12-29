package nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import newlang4.Environment;
import newlang4.LexicalType;

public class BlockNode extends Node {

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            addAll(IfBlockNode.FIRST_SET);
            addAll(LoopBlockNode.FIRST_SET);
        }
    };

    static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    //基本形 集合を作り、判定。パースは別のところ
    public static Node getHandler(Environment env) throws Exception {
        if (IfBlockNode.isMatch(env.getInput().peep().getType())) {
            return IfBlockNode.getHandler(env);
        } else if (LoopBlockNode.isMatch(env.getInput().peep().getType())) {
            return LoopBlockNode.getHandler(env);
        } else {
            throw new Exception("StmtNodeに不適切な型です。:" + env.getInput().peep().getType());
        }
    }

    public boolean parse() throws Exception {
        throw new Exception("BlockNodeクラスのparseは実行できません。");
    }

    public String toString() {
        return "BlockNode"; //表示されないはず
    }
}
