package newlang3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main3 {

    public static void main(String[] args) {
        String fname = "test1.bas"; //デフォネーム
        InputStream in = null;

        //ファイル名があれば読む
        if (args.length > 0) {
            fname = args[0];
        }

        //ファイル読み込み部
        try {
            in = new FileInputStream(fname);
        } catch (FileNotFoundException ex) {
            System.out.println(fname + " :Not Found！"); //ファイルがない時エラーメッセージ
            System.exit(-1);
        }

        //字句解析 結果表示部
        try {
            LexicalAnalyzer lanalyze = new LexicalAnalyzerImpl(in);
            while (true) {
                LexicalUnit lunit = lanalyze.get();
                System.out.println(lunit);

                if (lunit.getType() == LexicalType.EOF) {
                    break;
                }
            }

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            //ファイル閉じ部
            try {
                in.close();
                System.out.println("Finish!");
            } catch (IOException ex) {
                System.out.println(ex);
                System.out.println("InputStream Close Error");
            }
        }

    }
}
