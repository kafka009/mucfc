package com.mucfc;

import java.util.Objects;

public class Tuple3<O1, O2, O3> {
    private final O1 o1;
    private final O2 o2;
    private final O3 o3;

    public Tuple3(O1 o1, O2 o2, O3 o3) {
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
    }

    public O1 getO1() {
        return o1;
    }

    public O2 getO2() {
        return o2;
    }

    public O3 getO3() {
        return o3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(o1, tuple3.o1) && Objects.equals(o2, tuple3.o2) && Objects.equals(o3, tuple3.o3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o1, o2);
    }
}
