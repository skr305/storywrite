package util;

import dao.UserDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenTimeOutUtil {
    public static boolean isTimeOut(int sign, String token) throws ParseException {
        //sign=1，查询token
        //sign=2，查询refresh_token
        System.out.println(token);
        long timeLimit = sign == 1 ? 30 : 60 * 24 * 3;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserDao userDao = new UserDao();

        Date date1 = new Date();
        Date date2 = df.parse(userDao.getTokenTime(sign, token));
        long time = (date1.getTime() - date2.getTime()) / (1000 * 60);

        return time > timeLimit;
    }
}
