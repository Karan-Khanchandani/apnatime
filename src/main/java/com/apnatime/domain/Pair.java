package com.apnatime.domain;

import java.util.Objects;

public class Pair {

    private Integer first;
    private Integer second;

    public Pair(Integer first, Integer second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getSecond() {
        return second;
    }
}
