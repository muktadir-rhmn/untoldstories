package me.untoldstories.be.utils;

import me.untoldstories.be.error.exceptions.AssertionFailedException;

public class Assertion {
    public static void assertEqual(int n1, int n2) {
        if (n1 != n2) throw AssertionFailedException.EMPTY;
    }
}
