package newlang1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Main1 {

    public static void main(String[] args) {
        String fname = "test1.bas"; //デフォネーム
        //ファイル名があれば読む
        if (args.length > 0) {
            fname = args[0];
        }

        Reader fr = null;
        // BufferedReader br = null; //結局使わなかった
        try {
            //br = new BufferedReader(new FileReader(fname));
            fr = new FileReader(fname);
        } catch (FileNotFoundException e) {
            System.out.println(fname + " :ファイル読み取りエラー！"); //ファイルがない時エラーメッセージ
            System.exit(-1);
        }

        String str="";
        //1文字ずつ読んで表示
        while (true) {
            int c = 0;
            try {
                c = fr.read();
            } catch (IOException ex) {
                System.out.println("IO error");
                break;
            }
            if (c == -1) {
                break;
            }
            System.out.print((char) c);
            str +=(char)c;
            
        }
        System.out.println(str);

        //終了処理
        try {
            fr.close();
        } catch (IOException e) {
            //無視してもまあいいでしょう？
            System.out.println(e);
        }

        System.out.println("Finish!");
        
        System.out.println("fs3".toUpperCase());
        int c = 'a';
        System.out.println(String.valueOf(c));
    }
}
