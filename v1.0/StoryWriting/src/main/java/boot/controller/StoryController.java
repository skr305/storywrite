package boot.controller;

import dao.StoryDao;
import dao.UserDao;
import entity.Result;
import entity.Story;
import entity.StoryHead;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import util.CountUtil;
import util.TokenTimeOutUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@RestController
public class StoryController {

    @RequestMapping("/SDUstory/article/update/new")
    public Result newArticle(@RequestHeader("Token") String token) throws ParseException {
        UserDao userDao = new UserDao();
        StoryDao storyDao = new StoryDao();

        String username = userDao.selectByToken("username", token);
        StoryHead storyHead = new StoryHead();
        Story story = new Story();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (TokenTimeOutUtil.isTimeOut(1, token)) {
            return new Result(601, "token无效或不存在,请重新登录", null);
        } else {


            storyHead.setId(storyDao.getStoryId());
            storyHead.setStar(0);
            storyHead.setLike(0);
            storyHead.setDate(df.format(new Date()));
            storyHead.setMain_writer(userDao.selectNicknameByUsername(username));

            HashMap<String, Object> hashMap = storyDao.newHead(storyHead, token);

            return new Result(0, "success", hashMap);
        }
    }

    @RequestMapping("/SDUstory/article/tag")
    public Result tag() {
        Random random = new Random();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", random.nextInt(6));

        return new Result(0, "success", hashMap);
    }

    @RequestMapping("/SDUstory/article/update/title")
    public Result title(@RequestHeader("Token") String token, @RequestParam int id, @RequestParam String title) throws ParseException {
        StoryDao storyDao = new StoryDao();

        if (CountUtil.count(title) > 60)
            return new Result(5, "标题不能超过30个字符", null);
        else if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if (!storyDao.isWriter(token, id))
            return new Result(603, "权限不足", null);
        else {
            storyDao.updateTitle(title, id);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("/SDUstory/article/update/writing")
    public Result updateState(@RequestParam int id, @RequestParam boolean flag) {
        StoryDao storyDao = new StoryDao();
        storyDao.updateState(flag, id);
        return new Result(0, "success", null);
    }

    @RequestMapping("/SDUstory/article/update/ifwriting")
    public Result updateState(@RequestParam int id) {
        StoryDao storyDao = new StoryDao();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("flag", storyDao.getState(id));

        return new Result(0, "success", hashMap);
    }

    @RequestMapping("/SDUstory/article/update/content")
    public Result updateStory(@RequestHeader("Token") String token, @RequestParam int id, @RequestParam String content, @RequestParam int type) throws ParseException {
        UserDao userDao = new UserDao();
        StoryDao storyDao = new StoryDao();
        String username = userDao.selectByToken("username", token);
        String nickname = userDao.selectNicknameByUsername(username);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        Story story = new Story();
        story.setId(id);
        story.setType(type);
        story.setWriter(nickname);
        story.setLevel(storyDao.getMaxLevel(id) + 1);
        story.setDate(df.format(date));
        story.setContent(content);

        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if (story.getLevel() > 4)
            return new Result(4, "这个故事已经结尾啦", null);
        /** 为了使用自己的号 测试续写功能 不得不暂时注释掉这一块 */
//        else if (storyDao.hasWritten(nickname, id))
//            return new Result(3, "你已经提交过一次该故事啦", null);
        else if (storyDao.hasDeleted(id))
            return new Result(2, "故事已被删除", null);
        else {
            storyDao.insertStory(story, token);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("/SDUstory/article/delete")
    public Result deleteStory(@RequestHeader("Token") String token, @RequestParam int id) throws ParseException {
        StoryDao storyDao = new StoryDao();
        UserDao userDao = new UserDao();
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else if (!storyDao.isWriter(token, id) && !userDao.isAdmin(token))
            return new Result(603, "权限不足", null);
        else {
            storyDao.deleteStory(id);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("SDUstory/article/like")
    public Result like(@RequestHeader("Token") String token, @RequestParam int id) throws ParseException {
        StoryDao storyDao = new StoryDao();
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else {

            storyDao.like(id);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("SDUstory/article/delete/like")
    public Result cancelLike(@RequestHeader("Token") String token, @RequestParam int id) throws ParseException {
        StoryDao storyDao = new StoryDao();
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else {
            storyDao.cancelLike(id, token);
            return new Result(0, "success", null);
        }
    }


    @RequestMapping("SDUstory/article/star")
    public Result star(@RequestHeader("Token") String token, @RequestParam int id) throws ParseException {
        StoryDao storyDao = new StoryDao();
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else {
            storyDao.star(id, token);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("SDUstory/user/delete/favorites")
    public Result cancelStar(@RequestHeader("Token") String token, @RequestParam int id) throws ParseException {
        StoryDao storyDao = new StoryDao();
        if (TokenTimeOutUtil.isTimeOut(1, token))
            return new Result(601, "token无效或不存在,请重新登录", null);
        else {
            storyDao.cancelStar(id, token);
            return new Result(0, "success", null);
        }
    }

    @RequestMapping("SDUstory/article/list")
    public Result list() {
        StoryDao storyDao = new StoryDao();
        return new Result(0, "success", storyDao.getAll());
    }
}