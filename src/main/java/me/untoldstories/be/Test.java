package me.untoldstories.be;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class Test {
    public static void main(String[] args) {
        f("kk");
    }

    static void f(@Email @Valid String email) {
        System.out.println(email);
    }
}
