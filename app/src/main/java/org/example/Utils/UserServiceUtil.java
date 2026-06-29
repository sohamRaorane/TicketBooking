package org.example.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil {
    public static String hashPassword(String  password) {
        //Salting
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean checkPassword(String  password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
