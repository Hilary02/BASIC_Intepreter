package nodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import newlang4.Environment;
import newlang4.LexicalType;
import newlang4.LexicalUnit;

public class IfBlockNode extends Node {

    private Node cond; //condition  条件式 if_prefixの中身
    private Node trueOpe; // True時の処理
    private Node elseOpe; //Flase時の処理。Else
    boolean isNeedENDIF = false; //一番外側のIFだけENDIFが必要

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>(Arrays.asList(LexicalType.IF));

    private IfBlockNode(Environment env) {
        super(env);
        type = NodeType.IF_BLOCK;
    }

    static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    //判定して作成
    public static Node getHandler(Environment env) {
        return new IfBlockNode(env);
    }

    /*
    	<if_prefix> <stmt> <NL>
	| <if_prefix> <stmt> <ELSE> <stmt> <NL>
	| <if_prefix> <NL> <stmt_list> <else_block> <ENDIF> <NL>
	| <WHILE> <cond> <NL> <stmt_list> <WEND> <NL>
     */
    public boolean parse() throws Exception {
        //if_prefixチェック
        switch (env.getInput().peep().getType()) {
            case IF:
                //IFとELIFのちがいはこれだけ
                isNeedENDIF = true;
            case ELSEIF:
                env.getInput().get();
                if (CondNode.isMatch(env.getInput().peep().getType())) {
                    cond = CondNode.getHandler(env);
                    cond.parse();
                } else {
                    throw new Exception("IF文中に条件式がありません");
                }
                if (!env.getInput().expect(LexicalType.THEN)) {
                    throw new Exception("IF文中に\"THEN\"がありません");
                }
                env.getInput().get();
                //env.getInput().get();
                break;
            default:
                throw new Exception("IF文が呼ばれましたが\"IF\"もしくは\"ELSEIF\"がありません");
        }
//      <if_prefix> <stmt> <NL>
//	| <if_prefix> <stmt> <ELSE> <stmt> <NL> と
//	| <if_prefix> <NL> <stmt_list> <else_block> <ENDIF> <NL>
        if (StmtNode.isMatch(env.getInput().peep().getType())) {
            trueOpe = StmtNode.getHandler(env);
            trueOpe.parse();
            //| <if_prefix> <stmt> <ELSE> <stmt> <NL>
            if (env.getInput().expect(LexicalType.ELSE)) {
                env.getInput().get();

                if (StmtNode.isMatch(env.getInput().peep().getType())) {
                    elseOpe = StmtNode.getHandler(env);
                    elseOpe.parse();
                } else {
                    throw new Exception("ELSE文の処理中にエラーが起きました");
                }
            }
            if (!env.getInput().expect(LexicalType.NL)) {
                throw new Exception("IF文末に改行がありません");
            }
            env.getInput().get();
            return true;    //ここで終了
        } else if (env.getInput().expect(LexicalType.NL)) {
            env.getInput().get();
            //	| <if_prefix> <NL> <stmt_list> <else_block> <ENDIF> <NL>
            if (StmtListNode.isMatch(env.getInput().peep().getType())) {
                trueOpe = StmtListNode.getHandler(env);
                trueOpe.parse();
            } else {
                throw new Exception("ELSE文のTRUE時の処理の解析中にエラーが起きました");
            }
            while (env.getInput().expect(LexicalType.NL)) {
                env.getInput().get();   //ここ疑問。NL本当に要らないの？
            }

            //更に分岐。まだELSEIFが積み重なっているのかどうか。ELSEIFかELSEか。
            if (env.getInput().expect(LexicalType.ELSEIF)) {
                elseOpe = IfBlockNode.getHandler(env);
                elseOpe.parse();    //勝手に続けといてもらう
            } else if (env.getInput().expect(LexicalType.ELSE)) {
                env.getInput().get();
                if (!env.getInput().expect(LexicalType.NL)) {
                    throw new Exception("ELSE文直後に改行がありません");
                }
                skipNL();
                if (StmtListNode.isMatch(env.getInput().peep().getType())) {
                    elseOpe = StmtListNode.getHandler(env);
                    elseOpe.parse();
                } else {
                    throw new Exception("ELSE文のFALSE時の処理の解析中にエラーが起きました");
                }
                //ENDIF判定まで改行を飛ばす
                while (env.getInput().expect(LexicalType.NL)) {
                    env.getInput().get();   //ここ疑問。NL本当に要らないの？
                }
            }
            if (isNeedENDIF) {
                if (env.getInput().expect(LexicalType.ENDIF)) {
                    env.getInput().get();   //OK
                } else {
                    throw new Exception("ELSE節にENDIFがありません");
                }
                if (env.getInput().expect(LexicalType.NL)) {
                    env.getInput().get();   //OK
                } else {
                    throw new Exception("ELSE節の最後に改行がありません");
                }
            }
        } else {
            throw new Exception("条件文に続く適切な字句がありません");
        }
        return true;
    }

    public String toString() {
        String str = "";
        str += String.format("IF(%s)  THEN\n%s", cond, trueOpe);
        if (elseOpe != null) {
            str += String.format("\nELSE\n%s", elseOpe);
        }
        if (isNeedENDIF) {
            str += "\nENDIF\n";
        }
        return str;
    }
}
