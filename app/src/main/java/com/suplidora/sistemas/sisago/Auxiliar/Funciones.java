package com.suplidora.sistemas.sisago.Auxiliar;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sistemas on 24/5/2017.
 */

public class Funciones {


    public static String Codificar(String text) {
        return text.replace("+","(plus)")
                .replace("#","(hash)")
                .replace(".","(dot)")
                .replace("@","at")
                .replace("*","(star)")
                .replace("-","(minus)")
                .replace(",","(comma)")
                .replace(":","(semicolon)")
                .replace("!","(exclamation)")
                .replace("?","(question)")
                .replace("/","(slash)")
                .replace("~","(tilde)");
    }

    private static Pattern p = Pattern.compile("\\((\\d+)([+-]\\d{2})(\\d{2})\\)");

    public static Date jd2d(String jsonDateString) {
        Matcher m = p.matcher(jsonDateString);
        if (m.find()) {
            long millis = Long.parseLong(m.group(1));
            long offsetHours = Long.parseLong(m.group(2));
            long offsetMinutes = Long.parseLong(m.group(3));
            if (offsetHours < 0) offsetMinutes *= -1;
            return new Date(
                    millis
                            + offsetHours * 60l * 60l * 1000l
                            + offsetMinutes * 60l * 1000l
            );
        }
        return null;
    }
}