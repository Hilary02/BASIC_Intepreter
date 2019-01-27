package nodes;

import java.util.HashSet;
import java.util.Set;
import newlang5.*;

public class ProgramNode extends Node {

    //一応作成しておく。見ないけど。
    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            addAll(StmtListNode.FIRST_SET);
        }
    };

    public static Node getHandler(LexicalType t, Environment env) throws Exception {
        return StmtListNode.getHandler(env);
    }

    //表示のためのメソッド。それぞれのクラスに応じた適切な機能を書く
    //ProgramNodeを渡すことは無いので表示もされない。
    public String toString() {
        return "This is a Program Node.";
    }

    @Override
    public Value getValue() throws Exception {
        throw new Exception("ProgramNodeからgetValueはできません");
    }
}
