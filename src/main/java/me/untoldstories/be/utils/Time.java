package me.untoldstories.be.utils;

public final class Time {
    public static long curUnixEpoch() {
        return System.currentTimeMillis() / 1000;
    }
}
