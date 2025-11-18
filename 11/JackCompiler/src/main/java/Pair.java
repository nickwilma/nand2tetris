public class Pair<A extends Object, B extends Object, C extends Object> {

    private final A a;
    private final B b;
    private final C c;

    public Pair(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    public C getThird() {
        return c;
    }

    @Override
    public String toString() {
        return "<" + a.toString() + ", " + b.toString() + ", " + c.toString() + ">";
    }
}
