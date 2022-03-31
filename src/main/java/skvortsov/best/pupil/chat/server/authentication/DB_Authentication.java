package skvortsov.best.pupil.chat.server.authentication;

import java.sql.*;

public class DB_Authentication implements AuthenticationService {

    private static Connection connection;
    private static Statement stmt;

    public static void main(String[] args) throws SQLException {

        try {
            connection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        getAllUsers();


        disconnect();
    }

    @Override
    public void startAuthentication() {
        try {
            connection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" +
                "src/main/resources/skvortsov/best/pupil/chat/server/db/AUTH");
        stmt = connection.createStatement();
    }

    private static void disconnect() {
        closeStatement();
        closeConnection();
    }

    private static void closeStatement() {
        try {
            if (stmt != null){
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection() {
        try {
            if (connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {


        return null;
    }

    @Override
    public void endAuthentication() {
        disconnect();
    }

    private static void getAllUsers() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT  * FROM auth;");
        while (rs.next()){
//            System.out.printf("ID: %2s - Name: %8s%n",
//                    rs.getInt("id"), rs.getString("name"));

            System.out.printf("login: %4s - password: %8s < %9s >%n" ,
                    rs.getString("login"), rs.getString("password"), rs.getString("username"));
        }
    }
}
