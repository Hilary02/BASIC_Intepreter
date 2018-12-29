package nodes;

import newlang4.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ForStmtNode extends Node {

    private Node subst; //SUBSTを期待
    private LexicalUnit maxval;
    private Node operation;
    private LexicalUnit name;
// <FOR> <subst> <TO> <INTVAL> <NL> <stmt_list> <NEXT> <NAME>
    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(LexicalType.FOR));

    private ForStmtNode(Environment env) {
        super(env);
        type = NodeType.FOR_STMT;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new ForStmtNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        // check <FOR>
        if (!env.getInput().expect(LexicalType.FOR)) {
            throw new Exception("FOR文中にforがありません");
        }
        env.getInput().get();

        // check <subst>
        if (SubstNode.isMatch(env.getInput().peep().getType())) {
            this.subst = SubstNode.getHandler(env);
            subst.parse();
        } else {
            throw new Exception("FOR文の開始が不正です");
        }

        // check <TO>
        if (!env.getInput().expect(LexicalType.TO)) {
            throw new Exception("FOR文中にtoがありません");
        }
        env.getInput().get();

        // check <INTVAL>
        if (env.getInput().expect(LexicalType.INTVAL)) {
            maxval = env.getInput().get();
        } else {
            throw new Exception("FOR文中に終了値(整数)がありません");
        }

        // check <NL>
        if (!env.getInput().expect(LexicalType.NL)) {
            throw new Exception("処理部の前に改行がありません");
        }
        skipNL();

        // check <stmt_list>
        if (StmtListNode.isMatch(env.getInput().peep().getType())) {
            operation = StmtListNode.getHandler(env);
            operation.parse();
        } else {
            throw new Exception("FOR文中に適切な文がありません");
        }
        skipNL();

        // check <NEXT>
        if (!env.getInput().expect(LexicalType.NEXT)) {
            throw new Exception("FOR文中にNEXTがありません");
        }
        env.getInput().get();
        skipNL();

        // check <NAME>
        if (env.getInput().peep().getType() == LexicalType.NAME) {
            name = env.getInput().get();
        } else {
            throw new Exception("FOR文末に制御変数がありません");
        }
        return true;
    }

    public Value getValue() throws Exception {
        return null;
    }

    public String toString() {
        return String.format("FOR(%s TO %s ){\n%s\n}\n:%s", subst, maxval, operation, name);
    }
}
