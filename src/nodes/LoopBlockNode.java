package nodes;

import newlang4.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoopBlockNode extends Node {

    private Node cond; //condition  条件式
    private Node operation; // 継続時の処理
    private boolean isDoLoop; //do loop 処理であるかどうか
    private boolean condJudge; //condがT/Fどちらの時に継続するのか

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.DO,
            LexicalType.WHILE));

    private LoopBlockNode(Environment env) {
        super(env);
        type = NodeType.LOOP_BLOCK;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new LoopBlockNode(env);
    }

    @Override
    public boolean parse() throws Exception {

        if (env.getInput().expect(LexicalType.WHILE)) {
            isDoLoop = false;
            checkCond();
            checkOperation();

            //NLがあってもなくても次のWENDの判定につながる
            skipNL();
            if (!env.getInput().expect(LexicalType.WEND)) {
                throw new Exception("WHILE文末にWENDがありません");
            }
            env.getInput().get();
            if (!env.getInput().expect(LexicalType.NL)) {
                throw new Exception("WHILE文末に改行がありません");
            }
            env.getInput().get();

        } else if (env.getInput().peep().getType() == LexicalType.DO) {
            env.getInput().get();
            switch (env.getInput().peep().getType()) {
                case NL:
                    isDoLoop = true;
                    checkOperation();
                    if (!env.getInput().expect(LexicalType.LOOP)) {
                        throw new Exception("DO_LOOP構文の中に\"LOOP\"がありません");
                    }
                    env.getInput().get();

                    checkCond();
                    if (!env.getInput().expect(LexicalType.NL)) {
                        throw new Exception("DO_LOOP文末に改行がありません");
                    }
                    skipNL();
                    break;

                case WHILE:
                case UNTIL:
                    isDoLoop = false;
                    checkCond();
                    checkOperation();

                    if (!env.getInput().expect(LexicalType.LOOP)) {
                        throw new Exception(String.format("DO_%s_LOOP文末に\"LOOP\"がありません", condJudge ? "WHILE" : "UNTIL"));
                    }
                    env.getInput().get();

                    if (!env.getInput().expect(LexicalType.NL)) {
                        throw new Exception(String.format("DO_%s_LOOP文末に改行がありません", condJudge ? "WHILE" : "UNTIL"));
                    }
                    skipNL();

                    break;
                default:
                    throw new Exception("DO直後に適切な型がありません");
            }
        } else {
            throw new Exception("適切ではない型がLOOP処理の先頭に現れました:" + env.getInput().peep().getType());
        }

        return true;
    }

    public Value getValue() throws Exception {
        return null;
    }

    public String toString() {
        if (isDoLoop) {
            return String.format("%s{\n%s\n}\n%s%s\n",
                    isDoLoop ? "DO" : "LOOP",
                    operation,
                    condJudge ? "" : "!",
                    cond);
        } else {
            return String.format("%s%s%s{\n%s\n}\n",
                    isDoLoop ? "DO" : "LOOP",
                    condJudge ? "" : " !",
                    cond,
                    operation);
        }
    }

    private boolean checkCond() throws Exception {
        if (env.getInput().expect(LexicalType.WHILE)) {
            condJudge = true;   //WHILEなので真の間実行
        } else if (env.getInput().expect(LexicalType.UNTIL)) {
            condJudge = false;   //UNTILなので偽の間実行
        } else {
            throw new Exception("適切な条件判定がありません");
        }

        env.getInput().get();
        //制御分チェック
        if (CondNode.isMatch(env.getInput().peep().getType())) {
            cond = CondNode.getHandler(env);
            cond.parse();
        } else {
            throw new Exception(String.format("%s文中に制御文がありません", condJudge ? "WHILE" : "UNTIL"));
        }
        return true;
    }

    private boolean checkOperation() throws Exception {
        if (!env.getInput().expect(LexicalType.NL)) {
            throw new Exception("処理部の前に改行がありません");
        }
        skipNL();
        if (StmtListNode.isMatch(env.getInput().peep().getType())) {
            operation = StmtListNode.getHandler(env);
            operation.parse();
        } else {
            throw new Exception("繰り返し文中に処理部がありません");
        }
        return true;
    }
}
