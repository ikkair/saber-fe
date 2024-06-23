package com.desti.saber.utils;

import java.text.NumberFormat;
import java.util.Currency;

public class IDRFormatCurr {
    public static String currFormat(Long balance){
        NumberFormat currNf = NumberFormat.getCurrencyInstance();
        currNf.setMaximumFractionDigits(0);
        currNf.setCurrency(Currency.getInstance("IDR"));
        return currNf.format(balance).replace("Rp", "IDR ");
    }
}
