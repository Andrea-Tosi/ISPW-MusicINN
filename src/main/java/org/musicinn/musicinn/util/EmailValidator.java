package org.musicinn.musicinn.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    Pattern pattern = Pattern.compile(emailRegex);

    public Boolean isEmailFormatValid(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
