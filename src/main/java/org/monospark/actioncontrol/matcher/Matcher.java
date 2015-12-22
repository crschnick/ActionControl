package org.monospark.actioncontrol.matcher;

public interface Matcher<T> {

    boolean matches(T o);
}
