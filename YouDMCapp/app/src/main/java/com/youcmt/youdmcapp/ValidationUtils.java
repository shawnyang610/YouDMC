package com.youcmt.youdmcapp;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by Stanislav Ostrovskii on 11/13/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public final class ValidationUtils {
    /**
     * @param email
     * @return false if e-mail address entered is not well-formed
     * True does not guarantee a valid e-mail address
     */
    public static boolean isEmailAddressValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    /**
     * @param password
     * @return returns false is password is too short or does not contain one
     * lowercase or uppercase letter and one number. Otherwise returns true.
     */
    public static boolean isPasswordValid(String password)
    {
        if(password.length()<6) return false;

        //check to make sure password has one letter and one number
        boolean hasNum = false;
        boolean hasLetter = false;
        for(char c: password.toCharArray())
        {
            if((c>64 && c<91)||(c>96 && c<123))
                hasLetter = true;
            if(c>47 && c<58)
                hasNum = true;
        }
        if(!hasLetter||!hasNum) {
            return false;
        }
        return true;
    }

    /**
     * checks to see if two strings are equal.
     * @param one first string
     * @param two second string
     * @return true if they are equal, false otherwise.
     */
    public static boolean doPasswordsMatch(String one, String two)
    {
        return (one.equals(two));
    }
}

