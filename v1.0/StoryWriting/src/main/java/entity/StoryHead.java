package entity;

import org.jetbrains.annotations.NotNull;

public class StoryHead implements Comparable<StoryHead>{
    private String title;
    private String main_writer;
    private String date;
    private int like;
    private int star;
    private int id;

    public String getMain_writer() { return main_writer; }

    public void setMain_writer(String main_writer) { this.main_writer = main_writer; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(StoryHead storyHead) {
        int result = storyHead.like - this.like;
        if(result == 0)
            result = storyHead.star - this.star;
        if(result == 0) {
            if(this.date == null) {
                this.date = "2007-08-30";
            }
            if(storyHead.date == null) {
                storyHead.date = "2007-08-30";
            }

            result = storyHead.date.compareTo(this.date);
        }

        return result;
    }
}
