package checkers;

public class Pair {
    int a;
    int b;

    Pair(int p, int q) {
        a = p;
        b = q;
    }

    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair p = (Pair) o;
            return p.a == a && p.b == b;
        }
        return false;
    }

    public int hashCode() {
        return Integer.valueOf(a).hashCode() * 31 + Integer.valueOf(b).hashCode();
    }
}
