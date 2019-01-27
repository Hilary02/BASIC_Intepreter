package funcs;

import java.util.List;

import newlang5.Value;
import nodes.ExprListNode;
import nodes.Node;

public class PrintFunction extends Function {

    @Override
    public Value invoke(ExprListNode arg) {
        try {
            String s = arg.getChild().get(0).getValue().getSValue();
            System.out.print(s.replace("\\n", "\n"));
        } catch (Exception e) {
            System.out.println(e + "\nPRINT関数の第一引数が正しくありません");
        }
        if (arg.getChild().size() > 1) System.out.println("\tWARN:2つ以上の引数が渡されています\n");
        return null;
    }
}
