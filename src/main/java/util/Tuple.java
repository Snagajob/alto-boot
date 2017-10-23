package util;

/**
 * simple tuple-2 class
 *
 * @param <A> The first item in the tuple-2
 * @param <B> the second item in the tuple-2.
 */
public class Tuple<A,B> {
    public final A a;
    public final B b;

    public Tuple(A l, B r) {
        this.a = l;
        this.b = r;
    }

    public static <A,B> Tuple<A,B> create(A a, B b) {
        return new Tuple(a, b);
    }

    @Override
    public boolean equals(Object o) {
        Tuple<?,?> tuple = (Tuple<?,?>)o;
        return this.a.equals(tuple.a) && (this.b.equals(tuple.b));
    }
}
