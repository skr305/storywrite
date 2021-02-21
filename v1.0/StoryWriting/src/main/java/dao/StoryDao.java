package dao;

import entity.Story;
import entity.StoryHead;
import entity.User;
import util.MySQLDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StoryDao {

    public StoryHead getStoryHeadById(int id) {
        String sql = "SELECT * FROM story_head WHERE id=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            StoryHead storyHead = new StoryHead();
            storyHead.setTitle(resultSet.getString("title"));
            storyHead.setMain_writer(resultSet.getString("main_writer"));
            storyHead.setDate(resultSet.getString("date"));
            storyHead.setLike(resultSet.getInt("like"));
            storyHead.setStar(resultSet.getInt("star"));
            storyHead.setId(id);

            return storyHead;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public ArrayList<StoryHead> getList() {
        String sql = "SELECT * FROM story_head";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ArrayList<StoryHead> storyHeads = new ArrayList<>();
            StoryDao storyDao = new StoryDao();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (storyDao.getMaxLevel(id) == 4) {
                    StoryHead storyHead = new StoryHead();
                    storyHead.setId(id);
                    storyHead.setTitle(resultSet.getString("title"));
                    storyHead.setMain_writer(resultSet.getString("main_writer"));
                    storyHead.setDate(resultSet.getString("date"));
                    storyHead.setLike(resultSet.getInt("like"));
                    storyHead.setStar(resultSet.getInt("star"));

                    storyHeads.add(storyHead);
                }
            }

            Collections.sort(storyHeads);

            return storyHeads;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public int getMaxLevel(int id) {
        String sql = "SELECT * FROM story where id=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            int max = 0;
            while (resultSet.next())
                if (resultSet.getInt("level") > max)
                    max = resultSet.getInt("level");
            return max;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public ArrayList<Story> getStory(int id) {
        String sql = "SELECT * FROM story where id=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Story> stories = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("in GetStory");
                Story story = new Story();
                story.setContent(resultSet.getString("content"));
                story.setLevel(resultSet.getInt("level"));
                story.setWriter(resultSet.getString("writer"));
                story.setType(resultSet.getInt("type"));
                story.setDate(resultSet.getString("date"));
                story.setId(id);

                stories.add(story);
            }

            Collections.sort(stories);
            return stories;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public Map<String, Object> getStoryHead(int id) {
        String sql = "SELECT * FROM story_head WHERE id=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            Map<String, Object> storyHead = new HashMap<>();
            storyHead.put("title", resultSet.getString("title"));
            storyHead.put("main_writer", resultSet.getString("main_writer"));
            storyHead.put("content_id", resultSet.getInt("content"));
            storyHead.put("date", resultSet.getString("date"));
            storyHead.put("like", resultSet.getInt("like"));
            storyHead.put("star", resultSet.getInt("star"));
            storyHead.put("id", resultSet.getInt("id"));

            return storyHead;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public int getStoryId() {
        String sql = "SELECT * FROM story_head";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int result = 10000;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    if (resultSet.getInt("id") > result)
                        result = resultSet.getInt("id");
                }
            }
            return result + 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public HashMap<String, Object> newHead(StoryHead storyHead, String token) {
        String sql1 = "INSERT INTO story_head(content,date,`like`,star,id,main_writer) VALUES(?,?,?,?,?,?)";
        String sql2 = "SELECT * FROM user WHERE username=?";
        String sql3 = "UPDATE user SET articles=? WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        Random random = new Random();
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);
        ResultSet resultSet = null;

        try {
            /** 故事的开头序号 */
            int content_id = random.nextInt(5);

            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setInt(1, content_id);
            preparedStatement.setString(2, storyHead.getDate());
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, storyHead.getId());
            preparedStatement.setString(6, storyHead.getMain_writer());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String articles = resultSet.getString("articles");
            articles += storyHead.getId();

            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setString(1, articles);
            preparedStatement.setString(2, username);
            preparedStatement.execute();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("type", random.nextInt(5));
            hashMap.put("content", content_id);
            hashMap.put("id", storyHead.getId());

            return hashMap;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public boolean isWriter(String token, int id) {
        String sql = "SELECT * FROM story_head WHERE id=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserDao userDao = new UserDao();
        String nickname = userDao.selectNicknameByUsername(userDao.selectByToken("username", token));
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return nickname.equals(resultSet.getString("main_writer"));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public void updateTitle(String title, int id) {
        String sql = "UPDATE story_head SET title=? WHERE id=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, id);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public void updateState(boolean flag, int id) {
        String sql = "UPDATE story_head SET flag=? WHERE id=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        int flagNum = flag ? 1 : 0;

        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, flagNum);
            preparedStatement.setInt(2, id);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public boolean getState(int id) {
        String sql = "SELECT * FROM story_head WHERE id=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getInt("flag") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public boolean hasWritten(String nickname, int id) {
        String sql = "SELECT * FROM story WHERE id=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("writer").equals(nickname))
                    return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public boolean hasDeleted(int id) {
        String sql = "SELECT * FROM story_head WHERE id=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            return !resultSet.next();

        } catch (
                SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public void insertStory(Story story,String token) {
        String sql = "INSERT INTO story(writer,content,level,type,date,id) VALUES(?,?,?,?,?,?)";
        String sql2 = "SELECT * FROM user WHERE username=?";
        String sql3 = "UPDATE user SET favorites=? WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, story.getWriter());
            preparedStatement.setString(2, story.getContent());
            preparedStatement.setInt(3, story.getLevel());
            preparedStatement.setInt(4, story.getType());
            preparedStatement.setString(5, story.getDate());
            preparedStatement.setInt(6, story.getId());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String favorites = resultSet.getString("favorites");
            favorites += story.getId();

            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setString(1, favorites);
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public void deleteStory(int id) {

        String sql1 = "DELETE FROM story_head WHERE id=?";
        String sql2 = "DELETE FROM story WHERE id=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public void like(int id) {
        String sql1 = "SELECT * FROM story_head WHERE id=?";
        String sql2 = "UPDATE story_head SET `like`=? WHERE id=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int like = resultSet.getInt("like");
            like++;

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, like);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public void star(int id, String token) {
        String sql1 = "SELECT * FROM story_head WHERE id=?";
        String sql2 = "UPDATE story_head SET star=? WHERE id=?";
        String sql3 = "SELECT * FROM user WHERE username=?";
        String sql4 = "UPDATE user SET favorites=? WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);

        try {
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int star = resultSet.getInt("star");
            star++;

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, star);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String favorites = resultSet.getString("favorites");
            favorites += id;

            preparedStatement = connection.prepareStatement(sql4);
            preparedStatement.setString(1, favorites);
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public ArrayList<StoryHead> getAll() {
        String sql = "SELECT * FROM story_head";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ArrayList<StoryHead> storyHeads = new ArrayList<>();
            StoryDao storyDao = new StoryDao();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                StoryHead storyHead = new StoryHead();
                storyHead.setId(id);
                storyHead.setTitle(resultSet.getString("title"));
                storyHead.setMain_writer(resultSet.getString("main_writer"));
                storyHead.setDate(resultSet.getString("date"));
                storyHead.setLike(resultSet.getInt("like"));
                storyHead.setStar(resultSet.getInt("star"));

                storyHeads.add(storyHead);
            }

            Collections.sort(storyHeads);

            return storyHeads;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }


    public void cancelLike(int id, String token) {
        String sql1 = "SELECT * FROM story_head WHERE id=?";
        String sql2 = "UPDATE story_head SET `like`=? WHERE id=?";
        String sql3 = "SELECT * FROM user WHERE username=?";
        String sql4 = "UPDATE user SET likes=? WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);

        try {
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int like = resultSet.getInt("like");
            like--;

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, like);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            StringBuilder likes = new StringBuilder(resultSet.getString("likes"));
            int cnt = likes.length() / 5;
            for (int i = 0; i < cnt; ++i) {
                StringBuilder str = new StringBuilder();
                for (int j = i * 5; j < i * 5 + 5; j++)
                    str.append(likes.charAt(j));
                if (Integer.parseInt(str.toString()) == id) {
                    likes.delete(i * 5, i * 5 + 5);
                    break;
                }
            }
            String string = likes.toString();


            preparedStatement = connection.prepareStatement(sql4);
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }




    public void cancelStar(int id, String token) {
        String sql1 = "SELECT * FROM story_head WHERE id=?";
        String sql2 = "UPDATE story_head SET star=? WHERE id=?";
        String sql3 = "SELECT * FROM user WHERE username=?";
        String sql4 = "UPDATE user SET favorites=? WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);

        try {
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int star = resultSet.getInt("star");
            star--;

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, star);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(sql3);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            StringBuilder favorites = new StringBuilder(resultSet.getString("favorites"));
            int cnt = favorites.length() / 5;
            for (int i = 0; i < cnt; ++i) {
                StringBuilder str = new StringBuilder();
                for (int j = i * 5; j < i * 5 + 5; j++)
                    str.append(favorites.charAt(j));
                if (Integer.parseInt(str.toString()) == id) {
                    favorites.delete(i * 5, i * 5 + 5);
                    break;
                }
            }
            String string = favorites.toString();


            preparedStatement = connection.prepareStatement(sql4);
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }
}
