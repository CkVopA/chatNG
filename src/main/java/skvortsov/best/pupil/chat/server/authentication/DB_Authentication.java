package skvortsov.best.pupil.chat.server.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DB_Authentication implements AuthenticationService {

    private Connection connection;
    private Statement stmt;
    public static final Logger logger = LoggerFactory.getLogger(DB_Authentication.class);

    @Override
    public void startAuthentication() {
        try {
            connection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void connection() throws ClassNotFoundException, SQLException {
        logger.info("Подключение к базе данных . . .");
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" +
                "src/main/resources/skvortsov/best/pupil/chat/server/db/AUTH");
        stmt = connection.createStatement();
        logger.info("База данных подключена");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException {
        ResultSet rs = stmt.executeQuery(String.format(
                "SELECT * FROM auth WHERE login = '%s'", login));

            if (rs.isClosed()) return null;
            String username = rs.getString("username");
            String passwordDB = rs.getString("password");

            return (passwordDB.equals(password) ? username : null);
    }

    @Override
    public boolean changeUsername(String newNickname, String login){
        try {
            stmt.executeUpdate(String.format(
                    "UPDATE auth SET username = '%s' WHERE login = '%s'", newNickname, login));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private  void disconnect() {
        closeStatement();
        closeConnection();
    }

    private void closeStatement() {
        try {
            if (stmt != null){
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private  void closeConnection() {
        try {
            if (connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endAuthentication() {
        disconnect();
        logger.info("База данных отключена.");
    }
}
