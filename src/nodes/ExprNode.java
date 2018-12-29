package nodes;

import java.util.ArrayDeque;
import newlang4.*;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExprNode extends Node {

    private Node left;
    private Node right;
    private LexicalType operand;
    private boolean isMono = false;

    static final Set<LexicalType> FIRST_SET = new HashSet<LexicalType>() {
        {
            add(LexicalType.SUB);
            add(LexicalType.LP);
            add(LexicalType.NAME);//call_funcと共有
            add(LexicalType.INTVAL);
            add(LexicalType.DOUBLEVAL);
            add(LexicalType.LITERAL);
            addAll(FunctionCallNode.FIRST_SET);
        }
    };

    private final static Map<LexicalType, String> OPER_SET = new HashMap<>();

    static {
        OPER_SET.put(LexicalType.ADD, "+");
        OPER_SET.put(LexicalType.SUB, "-");
        OPER_SET.put(LexicalType.MUL, "*");
        OPER_SET.put(LexicalType.DIV, "/");
    }

    private ExprNode(Environment env) {
        super(env);
        type = NodeType.EXPR;
    }

    //-Xみたいな単項式用
    private ExprNode(Environment env, boolean b) {
        super(env);
        type = NodeType.EXPR;
        isMono = b;
    }

    private ExprNode(Node l, Node r, LexicalType o) {
        type = NodeType.EXPR;
        left = l;
        right = r;
        operand = o;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST_SET.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new ExprNode(env);
    }

    //逆ポーランド記法を作る
    @Override
    public boolean parse() throws Exception {
        // 結果格納用
        Deque<Node> exprStack = new ArrayDeque<>();
        Deque<LexicalUnit> operStack = new ArrayDeque<>();

        while (true) {
            //次の項(or演算子)が何者なのか知る
            switch (env.getInput().peep().getType()) {
                case INTVAL:
                case DOUBLEVAL:
                case LITERAL:
                    exprStack.add(ConstNode.getHandler(env.getInput().get().getValue()));
                    break;
                case LP:
                    env.getInput().get();
                    Node bracket = ExprNode.getHandler(env);
                    bracket.parse();
                    exprStack.add(bracket); //1つの項として突っ込んでしまう
                    if (env.getInput().expect(LexicalType.RP)) {
                        env.getInput().get();
                    } else {
                        throw new Exception("式中の括弧の対応が正しくないです。閉じる括弧が不足しています");
                    }
                    break;
                case SUB:
                    env.getInput().get();
                    if (!ExprNode.isMatch(env.getInput().peep().getType())) {
                        throw new Exception("-の後に続く式がありません");
                    }
                    Node subNode = new ExprNode(env, true);
                    subNode.parse();
                    exprStack.add(subNode); //1つの項として突っ込んでしまう。-の処理はそこでする
                    break;
                case NAME:
                    if (env.getInput().peep(2).getType() == LexicalType.LP) {   //関数呼び出しのとき
                        Node funcNode = FunctionCallNode.getHandler(env);
                        funcNode.parse();
                        exprStack.add(funcNode);

                    } else {
                        exprStack.add(new VariableNode(env.getInput().get().getValue().getSValue()));   //ただの変数
                    }
                    break;
                default:
                    throw new Exception("式の項として識別できない型です:" + env.getInput().peep().getType());
            }

            //単項式のときはここで打ち切り。項だけ
            if (isMono) {
                //計算時用に-1がかけられたものとする
                left = ConstNode.getHandler(new ValueImpl(-1));
                operand = LexicalType.MUL;
                right = exprStack.pollLast();
                return true;
            }

            //項の次には必ず符号。ないなら式終了。
            //新しい符号が優先度高いならそのまま追加。低いか同程度なら前の符号と項でExprNodeを生成
            if (OPER_SET.containsKey(env.getInput().peep().getType())) {
                if (operStack.size() > 0
                        && getOpePriority(env.getInput().peep().getType()) <= getOpePriority(operStack.peekLast().getType())) {
                    //ex) * * や * + の順で来た時
                    Node right = exprStack.pollLast();
                    Node left = exprStack.pollLast();
                    LexicalType operand = operStack.pollLast().getType();
                    exprStack.add(new ExprNode(left, right, operand));
                    operStack.add(env.getInput().get());

                } else {
                    // ex) + * のようなとき、まだ続くかもしれないので足すだけ
                    operStack.add(env.getInput().get());
                }
            } else {
                break; //そこで式が打ち切り。stackを解消しに行く
            }
        }
        //式の洗い出しが終わった.
        int nOper = operStack.size();
        for (int i = 0; i < nOper; i++) {
            Node l = exprStack.pollFirst();
            Node r = exprStack.pollFirst();
            LexicalType op = operStack.pollFirst().getType();
            exprStack.addFirst(new ExprNode(l, r, op));
        }
        left = exprStack.pollLast();

        return true;
    }

    public Value getValue() throws Exception {
        return null;
    }

    public String toString() {
        if (operand == null) {
            return "" + left;
        } else if (isMono) {
            return "-" + right;
        } else {
            return String.format("(%s)(%s)%s", left, right, OPER_SET.get(operand));
        }
    }

    //結合の優先度を返す
    private int getOpePriority(LexicalType type) {
        switch (type) {
            case MUL:
            case DIV:
                return 2;
            case SUB:
            case ADD:
                return 1;
            default:
                return -1;
        }
    }
}
