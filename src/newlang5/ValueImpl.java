package newlang5;

public class ValueImpl implements Value {

    private ValueType type;
    private String value;

    public ValueImpl(String s) {
        type = ValueType.STRING;
        value = s;
    }

    public ValueImpl(int i) {
        type = ValueType.INTEGER;
        value = i + "";
    }

    public ValueImpl(double d) {
        type = ValueType.DOUBLE;
        value = d + "";
    }

    public ValueImpl(boolean b) {
        type = ValueType.BOOL;
        value = b + "";
    }

    @Override
    public String getSValue() {
        return value;
    }

    @Override
    public int getIValue() {
        return Integer.parseInt(value);
    }

    @Override
    public double getDValue() {
        return Double.parseDouble(value);
    }

    @Override
    public boolean getBValue() {
        return Boolean.parseBoolean(value); //プリミティブ型を返すやつ
    }

    @Override
    public ValueType getType() {
        return type;
    }

    public String toString() {
        return getSValue();
    }
}
