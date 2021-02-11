package boot.controller;

import dao.UserDao;
import entity.Result;
import entity.StoryHead;
import entity.User;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.CountUtil;
import util.TokenTimeOutUtil;
import util.UploadUtil;
import util.login.CourseSelectCasLogin;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    @RequestMapping("/SDUstory/auth/login")
    public Result login(@RequestParam String username, @RequestParam String password) throws IOException {
        CourseSelectCasLogin courseSelectCasLogin = new CourseSelectCasLogin();
        String cookie = courseSelectCasLogin.serve(username, password);

        if (cookie.length() == "JSESSIONID=abcIWbGdiQZypewUVgwCx;".length()) {
            String token = UUID.randomUUID() + "";
            String refreshToken = UUID.randomUUID() + "";
            UserDao userDao = new UserDao();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            userDao.deleteToken(username);
            userDao.recordToken(username, token, refreshToken, df.format(date), df.format(date));

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("token", token);
            tokens.put("refresh_token", refreshToken);

            return new Result(0, "success", tokens);

        } else {
            return new Result(1, "用户名或密码错误", null);
        }
    }

    @RequestMapping("/SDUstory/auth/refresh")
    public Result refresh(@RequestHeader("Token") String token) throws ParseException {
        UserDao userDao = new UserDao();
        String refreshToken = userDao.selectByToken("refresh_token", token);

        if (TokenTimeOutUtil.isTimeOut(2, refreshToken))
            return new Result(602, "refresh_token无效或已过期", null);
        else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String newToken = UUID.randomUUID() + "";
            userDao.updateToken(token, newToken, df.format(date));

            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("new_token", newToken);

            return new Result(0, "success", tokenMap);
        }
    }

    @RequestMapping("/SDUstory/user/info/base")
    public Result baseInfo(@RequestHeader("Token") String token) throws ParseException {
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);
        if (TokenTimeOutUtil.isTimeOut(1, token)) {
            return new Result(601, "token无效或不存在,请重新登录", null);
        } else {
            User user = userDao.getBaseInfo(username);
            return new Result(0, "success", user);
        }
    }

    @RequestMapping("/SDUstory/user/info/favorites")
    public Result favoritesInfo(@RequestHeader("Token") String token) throws ParseException {
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if (userDao.noArticles(username, "favorites"))
            return new Result(1, "收藏列表为空", null);
        else {
            StoryHead[] storyHeads = userDao.getFavoritesInfo(username);
            return new Result(0, "success", storyHeads);
        }
    }

    @RequestMapping("/SDUstory/user/info/articles")
    public Result articlesInfo(@RequestHeader("Token") String token) throws ParseException {
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if (userDao.noArticles(username, "articles"))
            return new Result(2, "创作列表为空", null);
        else {
            StoryHead[] storyHeads = userDao.getArticlesInfo(username);
            return new Result(0, "success", storyHeads);
        }
    }

    @RequestMapping("/SDUstory/user/update/new")
    public Result setNickname(@RequestHeader("Token") String token, @RequestParam String nickname) throws ParseException {
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if (CountUtil.count(nickname) > 16)
            return new Result(4, "昵称不能超过16个字符", null);
        else if (userDao.nicknameIsUsed(nickname))
            return new Result(3, "该昵称已被占用", null);
        else {
            userDao.setNickname(username, nickname);
            return new Result(0, "success", null);
        }

    }

    @RequestMapping("/SDUstory/user/update/image")
    public Result setImage(@RequestHeader("Token") String token, @RequestParam("image") MultipartFile multipartFile) throws Exception {
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if ((double) multipartFile.getSize() / 1024 > 200)
            return new Result(5, "头像大小不能超过200KB", null);
        else {
            String imageURL = UploadUtil.uploadImage(multipartFile);
            userDao.setImage(username, imageURL);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("/SDUstory/user/select/nickname")
    public Result selectNickname(@RequestHeader("Token") String token) throws ParseException {
        UserDao userDao = new UserDao();
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else {
            boolean flag = userDao.hasName(userDao.selectByToken("username", token));
            return new Result(0, "success", flag);
        }
    }

    @RequestMapping("/SDUstory/user/select/like")
    public Result selectLike(@RequestHeader("Token") String token, int id) {
        UserDao userDao = new UserDao();
        return new Result(0, "success", userDao.selectByStoryId("likes", id, token));
    }

    @RequestMapping("/SDUstory/user/select/favorite")
    public Result selectFavorite(@RequestHeader("Token") String token, int id) {
        UserDao userDao = new UserDao();
        return new Result(0, "success", userDao.selectByStoryId("favorites", id, token));
    }
}