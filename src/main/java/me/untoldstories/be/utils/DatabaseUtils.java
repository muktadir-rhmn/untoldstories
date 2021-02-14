package me.untoldstories.be.utils;

import java.util.List;

public class DatabaseUtils {
    public static String makeStringList(List<Long> nums) {
        StringBuilder sb = new StringBuilder();
        for (long num: nums) {
            sb.append(num).append(',');
        }
        sb.insert(sb.length() - 1, ' ');
        return sb.toString();
    }
}
