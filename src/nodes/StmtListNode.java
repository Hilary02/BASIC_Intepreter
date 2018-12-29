package nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import newlang4.Environment;
import newlang4.LexicalType;
import newlang4.LexicalUnit;
import newlang4.Value;

public class StmtListNode extends Node {

    List<Node> child = new ArrayList<Node>(); //ジェネリクスも書くこと

    //first集合のsetはどのクラスにも必要。
    /*
    <stmt_list>　::=
	<stmt>
	| <stmt_list> <NL> <stmt>
	| <block>
	| <stmt_list> <block>
     */
    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            addAll(StmtNode.FIRST_SET);
            addAll(BlockNode.FIRST_SET);
        }
    };

    //コンストラクタもprivateでsuper呼べばOK
    //Environmentってなんだ？
    private StmtListNode(Environment env) {
        super(env);
        type = NodeType.STMT_LIST;
    }

    //渡されたものが自分の先頭要素ならTrue
    static boolean isMatch(LexicalType t) {
        return FIRST_SET.contains(t);
    }

    //getHandlerの呼び出し元で正しいかを判定していることを想定するのでここでは作成のみ
    public static Node getHandler(Environment env) {
        return new StmtListNode(env);
    }

    //ENDまで読み込むことができればTrue。途中で失敗したらFalse.
    public boolean parse() throws Exception {
        while (true) {
            //NLは読み飛ばす
            skipNL();
            Node candidate;
            //ここは1段で特定できる
            if (StmtNode.isMatch(env.getInput().peep().getType())) {
                candidate = StmtNode.getHandler(env);
            } else if (BlockNode.isMatch(env.getInput().peep().getType())) {
                candidate = BlockNode.getHandler(env);
            } else {
                break; //そこで解析は終了
            }
            //パースに失敗した時はExceptionが飛ぶがここでは処理しない
            candidate.parse();
            child.add(candidate);
        }
        return true;
    }

    //今回は作らない。プログラムを実行するメソッド
    //ExprのgetValueは式の値を返してくれる
    public Value getValue() throws Exception {
        return null;
    }

    //表示のためのメソッド。それぞれのクラスに応じた適切な機能を書く
    public String toString() {
        String str = "";
        for (int i = 0; i < child.size(); i++) {
            str += child.get(i).toString();
            if (i != child.size() - 1) {
                str += "\n";
            }
        }
        return str;
    }
}
