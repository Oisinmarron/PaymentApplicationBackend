package com.ezycollect.paymentapplication.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidator {

    public static boolean isValidUrl(final String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ex) {
            return false;
        }
    }
}
