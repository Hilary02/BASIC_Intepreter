package newlang4;

import nodes.Node;
import nodes.ProgramNode;
import java.io.FileInputStream;

public class Main4 {

    public static void main(String[] args) throws Exception {
        FileInputStream fin = null;
        LexicalAnalyzer lex;
        LexicalUnit first;
        Environment env;
        Node program;

        System.out.println("==basic parser==");
        fin = new FileInputStream("test1.bas");
        lex = new LexicalAnalyzerImpl(fin);
        env = new Environment(lex);
        first = lex.get();
        lex.unget(first);

        //program = Program.isMatch(env, first);
        program = null;//ProgramNode.getHandler((first.getType()), env); //Newlang5のためにコメントアウト
        if (program != null && program.parse()) {   //programが存在し、かつparseできたとき
            System.out.println(program);
            //  System.out.println("value = " + program.getValue());
            //これは後ででもいい
        } else {
            System.out.println("syntax error");
        }
        System.out.println("==Finish!==");
    }
}
