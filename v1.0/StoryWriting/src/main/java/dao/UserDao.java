package dao;

import entity.Result;
import entity.StoryHead;
import entity.User;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import util.MySQLDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public void recordToken(String username, String token, String refreshToken, String tokenTime, String refreshTokenTime) {

        String sql1 = "DELETE FROM token WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        String sql2 = "INSERT INTO token(username,token,refresh_token,token_time,refresh_token_time) VALUES(?,?,?,?,?)";
        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = connection.prepareStatement(sql1);

            preparedStatement.setString(1, username);

            preparedStatement.execute();


            preparedStatement = connection.prepareStatement(sql2);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, token);
            preparedStatement.setString(3, refreshToken);
            preparedStatement.setString(4, tokenTime);
            preparedStatement.setString(5, refreshTokenTime);

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public String getTokenTime(int sign, String token) {
        //sign=1，查询token
        //sign=2，查询refresh_token

        String sql = sign == 1 ? "SELECT * FROM token WHERE token=?" : "SELECT * FROM token WHERE refresh_token=?";
        String time = sign == 1 ? "token_time" : "refresh_token_time";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            System.out.println("token:" + token);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getString(time);

        } catch (SQLException e) {
            e.printStackTrace();
            return "getTokenTimeERROR";
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public String selectByToken(String key, String token) {

        String sql = "SELECT * FROM token WHERE token=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getString(key);

        } catch (SQLException e) {
            e.printStackTrace();
            return "selectByTokenERROR";
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public String selectNicknameByUsername(String username) {

        String sql = "SELECT nickname FROM user WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getString("nickname");

        } catch (SQLException e) {
            e.printStackTrace();
            return "selectNicknameByUsernameERROR";
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public void updateToken(String token, String newToken, String newDate) {
        String sql = "UPDATE token SET token=?,token_time=? WHERE token=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, newToken);
            preparedStatement.setString(2, newDate);
            preparedStatement.setString(3, token);

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public void deleteToken(String username) {
        String sql = "DELETE FROM token WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public User getBaseInfo(String username) {




        String sql = "SELECT * FROM user WHERE username=?";



        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();


            /** 看看是不是还没有开户记录 */
            /** 若还未有开户记录 则初始化 */
            if(!resultSet.next()) {
                System.out.println("in Init");
                String initSql =  "insert into user (username,image,nickname,favorites, articles) values(?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(initSql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, "");
                preparedStatement.setString(3, "");
                preparedStatement.setString(4, "");
                preparedStatement.setString(5, "");
                preparedStatement.executeUpdate();
            }



            /** 再次进行查询 此时不会再出现找不到记录的问题 */
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            User user = new User();
            user.setUsername(username);
            user.setImage(resultSet.getString("image"));
            user.setNickname(resultSet.getString("nickname"));

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public boolean noArticles(String username, String key) {
        String sql = "SELECT * FROM user WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String str = resultSet.getString(key);
            return str == null || str.equals("");
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public StoryHead[] getFavoritesInfo(String username) {
        String sql = "SELECT * FROM user WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StoryDao storyDao = new StoryDao();

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String favorites = resultSet.getString("favorites");
            int cnt = favorites.length() / 5;
            StoryHead[] storyHeads = new StoryHead[cnt];

            for (int i = 0; i < cnt; ++i) {
                StringBuilder id = new StringBuilder();
                for (int j = i * 5; j < i * 5 + 5; j++)
                    id.append(favorites.charAt(j));
                if (!storyDao.hasDeleted(Integer.parseInt(id.toString())))
                    storyHeads[i] = storyDao.getStoryHeadById(Integer.parseInt(id.toString()));
            }

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

    public StoryHead[] getArticlesInfo(String username) {
        String sql = "SELECT * FROM user WHERE username=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StoryDao storyDao = new StoryDao();

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String favorites = resultSet.getString("articles");
            int cnt = favorites.length() / 5;

            StoryHead[] storyHeads = new StoryHead[cnt];

            for (int i = 0; i < cnt; ++i) {
                StringBuilder id = new StringBuilder();
                for (int j = i * 5; j < i * 5 + 5; j++)
                    id.append(favorites.charAt(j));
                if (!storyDao.hasDeleted(Integer.parseInt(id.toString())))
                    storyHeads[i] = storyDao.getStoryHeadById(Integer.parseInt(id.toString()));
            }

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

    public boolean nicknameIsUsed(String nickname) {
        String sql = "SELECT * FROM user WHERE nickname=?";

        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nickname);
            resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public void setNickname(String username, String nickname) {
        String sql = "UPDATE user SET nickname=? WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, username);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public void setImage(String username, String imageURL) {
        String sql = "UPDATE user SET image=? WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, imageURL);
            preparedStatement.setString(2, username);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
        }
    }

    public boolean hasName(String username) {
        String sql = "SELECT * FROM user WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            String str = resultSet.getString("nickname");
            return !(str == null || str.equals(""));
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public boolean isAdmin(String token) {
        String sql = "SELECT * FROM admin WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, this.selectByToken("username", token));
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLDriver.close(connection);
            MySQLDriver.close(preparedStatement);
            MySQLDriver.close(resultSet);
        }
    }

    public boolean selectByStoryId(String key, int id, String token) {
        String sql = "SELECT * FROM user WHERE username=?";
        Connection connection = MySQLDriver.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserDao userDao = new UserDao();
        String username = userDao.selectByToken("username", token);

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            String idString = resultSet.getString(key);
            int cnt = idString.length() / 5;
            for (int i = 0; i < cnt; ++i) {
                StringBuilder str = new StringBuilder();
                for (int j = i * 5; j < i * 5 + 5; j++)
                    str.append(idString.charAt(j));
                if (Integer.parseInt(str.toString()) == id)
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

}
