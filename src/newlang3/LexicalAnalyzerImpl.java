package newlang3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

    //記号と予約語をそれぞれまとめたmap
    private static final Map<String, LexicalUnit> SYMBOL_MAP = new HashMap<>();
    private static final Map<String, LexicalUnit> RESERVED_MAP = new HashMap<>();

    PushbackReader pbReader;

    //マップの定義
    static {
        SYMBOL_MAP.put("=", new LexicalUnit(LexicalType.EQ));
        SYMBOL_MAP.put("<", new LexicalUnit(LexicalType.LT));
        SYMBOL_MAP.put(">", new LexicalUnit(LexicalType.GT));
        SYMBOL_MAP.put(".", new LexicalUnit(LexicalType.DOT));
        SYMBOL_MAP.put("+", new LexicalUnit(LexicalType.ADD));
        SYMBOL_MAP.put("-", new LexicalUnit(LexicalType.SUB));
        SYMBOL_MAP.put("*", new LexicalUnit(LexicalType.MUL));
        SYMBOL_MAP.put("/", new LexicalUnit(LexicalType.DIV));
        SYMBOL_MAP.put("(", new LexicalUnit(LexicalType.LP));
        SYMBOL_MAP.put(")", new LexicalUnit(LexicalType.RP));
        SYMBOL_MAP.put(",", new LexicalUnit(LexicalType.COMMA));
        SYMBOL_MAP.put("<=", new LexicalUnit(LexicalType.LE));
        SYMBOL_MAP.put("=<", new LexicalUnit(LexicalType.LE));
        SYMBOL_MAP.put("=>", new LexicalUnit(LexicalType.GE));
        SYMBOL_MAP.put(">=", new LexicalUnit(LexicalType.GE));
        SYMBOL_MAP.put("<>", new LexicalUnit(LexicalType.NE));
        SYMBOL_MAP.put("\r", new LexicalUnit(LexicalType.NL));
        SYMBOL_MAP.put("\n", new LexicalUnit(LexicalType.NL));
        //実行環境依存を解決...
        SYMBOL_MAP.put(System.getProperty("line.separator"), new LexicalUnit(LexicalType.NL));
        RESERVED_MAP.put("IF", new LexicalUnit(LexicalType.IF));
        RESERVED_MAP.put("THEN", new LexicalUnit(LexicalType.THEN));
        RESERVED_MAP.put("ELSE", new LexicalUnit(LexicalType.ELSE));
        RESERVED_MAP.put("ELSEIF", new LexicalUnit(LexicalType.ELSEIF));
        RESERVED_MAP.put("ENDIF", new LexicalUnit(LexicalType.ENDIF));
        RESERVED_MAP.put("FOR", new LexicalUnit(LexicalType.FOR));
        RESERVED_MAP.put("FORALL", new LexicalUnit(LexicalType.FORALL));
        RESERVED_MAP.put("NEXT", new LexicalUnit(LexicalType.NEXT));
        RESERVED_MAP.put("FUNC", new LexicalUnit(LexicalType.FUNC));
        RESERVED_MAP.put("DIM", new LexicalUnit(LexicalType.DIM));
        RESERVED_MAP.put("AS", new LexicalUnit(LexicalType.AS));
        RESERVED_MAP.put("END", new LexicalUnit(LexicalType.END));
        RESERVED_MAP.put("WHILE", new LexicalUnit(LexicalType.WHILE));
        RESERVED_MAP.put("DO", new LexicalUnit(LexicalType.DO));
        RESERVED_MAP.put("UNTIL", new LexicalUnit(LexicalType.UNTIL));
        RESERVED_MAP.put("LOOP", new LexicalUnit(LexicalType.LOOP));
        RESERVED_MAP.put("TO", new LexicalUnit(LexicalType.TO));
        RESERVED_MAP.put("WEND", new LexicalUnit(LexicalType.WEND));
    }

    //Readerを生成。存在しないファイルは来ない。
    public LexicalAnalyzerImpl(InputStream in) {
        pbReader = new PushbackReader(new InputStreamReader(in));
    }

    @Override
    public LexicalUnit get() throws Exception {
        int ci;
        do {
            ci = pbReader.read();
        } while (ci == ' ' || ci == '\t');
        //ファイル終端判定
        if (ci == -1) {
            return new LexicalUnit(LexicalType.EOF);
        } else {
            pbReader.unread(ci);
        }

        //LITERAL
        if (ci == '\"') {
            return getLiteral();
        }
        //NUMBER
        if ('0' <= ci && ci <= '9') {
            return getNumber();
        }
        //WORD
        if (('a' <= ci && ci <= 'z') || ('A' <= ci && ci <= 'Z')) {
            return getString();
        }
        //SYMBOL
        if (SYMBOL_MAP.containsKey(String.valueOf((char) ci))) {
            return getSymbol();
        }

        //それ以外の文字。マルチバイトとか変な記号とか来たら返す
        throw new Exception("使用できない文字が含まれています");

    }

    private LexicalUnit getLiteral() throws Exception {
        String val = "";
        int ci = pbReader.read();   // "を読み飛ばす

        while (true) {
            ci = pbReader.read();
            if (ci < 0) {
                throw new Exception("Literalの解析中にファイル終端に達しました");
            } else if (ci == '\n') {
                throw new Exception("Literalの解析中に改行が行われました");
            } else if (ci == '\"') {
                break;  //読み飛ばし、return処理へ
            } else {
                val += (char) ci;
            }
        }
        return new LexicalUnit(LexicalType.LITERAL, new ValueImpl(val));
    }

    private LexicalUnit getNumber() throws Exception {
        boolean isUsedDecimalPoint = false;
        String val = "";
        //数字とピリオドであるか判定。2度ピリオドが含まれないか確認
        //3. とか.で終わるものも許可。parseDoubleも通る。
        while (true) {
            int ci = pbReader.read();
            if ('0' <= ci && ci <= '9') {
                val += (char) ci;
            } else if (ci == '.') {
                if (isUsedDecimalPoint) {
                    throw new Exception("小数点が2度使用されています");
                } else {
                    val += (char) ci;
                    isUsedDecimalPoint = true;
                }
            } else if (ci == -1) {
                //EOFを書き戻すと65535になってしまう。
                break;
            } else {
                pbReader.unread(ci);
                break;
            }
        }

        if (isUsedDecimalPoint) {
            return new LexicalUnit(LexicalType.DOUBLEVAL, new ValueImpl(Double.parseDouble(val)));
        } else {
            return new LexicalUnit(LexicalType.INTVAL, new ValueImpl(Integer.parseInt(val)));
        }
    }

    // WORD  文字例(数字含む)が続く限りそれを読み続けるメソッド。 
    private LexicalUnit getString() throws Exception {
        //長いString が伸びていく。数字の場合も伸びていくようになる。
        //BASICでは大文字小文字が区別されない。のでそれに倣って全て大文字に変換。
        String str = "";

        while (true) {
            int ci = pbReader.read();
            if (ci < 0) {
                break;
            }
            //WORD
            if (('a' <= ci && ci <= 'z') || ('A' <= ci && ci <= 'Z') || ('0' <= ci && ci <= '9')) {
                str += (char) ci;
            } else {
                pbReader.unread(ci);
                break;
            }
        }
        str = str.toUpperCase();
        //ここでmap引いて判断
        if (RESERVED_MAP.containsKey(str)) {
            return RESERVED_MAP.get(str);
        } else {
            return new LexicalUnit(LexicalType.NAME, new ValueImpl(str));
        }

        //これが一番大事。でもこれだと予約語に対応できてない。
        //予約語かどうかは1行で済む。
        //予約語表は別で作るけど判定は
        //1行にまとめる? map
    }

    private LexicalUnit getSymbol() throws Exception {
        //1文字マッチしたら2文字、3文字と調べていく
        String symbol = "";
        int ci = 0;
        while (true) {
            ci = pbReader.read();
            if (ci < 0) {
                break;
                //throw new Exception("記号解析中にファイル終端に達しました");
            }
            if (SYMBOL_MAP.containsKey(symbol + (char) ci)) {
                symbol += (char) ci;
            } else {
                pbReader.unread(ci);
                break;
            }
        }

        return SYMBOL_MAP.get(symbol);
    }

    @Override
    //型を要求？期待？
    public boolean expect(LexicalType type) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    //トークンの破棄？
    public void unget(LexicalUnit token) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
