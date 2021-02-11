package boot.controller;

import dao.StoryDao;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomepageController {

    @RequestMapping("/SDUstory/homepage/list")
    public Result list(){
        StoryDao storyDao = new StoryDao();
        return new Result(0,"success",storyDao.getList());
    }

    @RequestMapping("/SDUstory/homepage/storyhead")
    public Result story(@RequestParam int id){
        StoryDao storyDao = new StoryDao();
        return new Result(0,"success",storyDao.getStoryHead(id));
    }

    @RequestMapping("/SDUstory/homepage/story")
    public Result storyHead(@RequestParam int id){
        StoryDao storyDao = new StoryDao();
        return new Result(0,"success",storyDao.getStory(id));
    }
}