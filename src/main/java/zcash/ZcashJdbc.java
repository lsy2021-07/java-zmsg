package zcash;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZcashJdbc {

    private static final String url = "jdbc:mysql://sh-cynosdbmysql-grp-3fwf5ks8.sql.tencentcdb.com:28414/blockchain";

    private static final String user = "root";

    private static final String password = "Shunine8";

    private static final String driver = "com.mysql.cj.jdbc.Driver";

//    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//
//        insert_test();
////        check_test();
//    }

    public static void insert_test(){
        Object[] objects = {"test_txid1","test_add1","test_add3"};
        System.out.println("添加数据：");
        int rows = ZcashJdbc.insert(objects);
    }

    public static void delete_test(){
        String key = "test_txid";
        System.out.println("删除数据：");
        int rows = ZcashJdbc.delete(key);
    }

    public static void check_test(){
        String key = "test_txid";
        String add1 = "test_add1";

        System.out.println("查询数据：");
        System.out.println(ZcashJdbc.checkByAdd1(add1));
    }

    public static Connection _getConnection(){
        Connection con = null;
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    /**
     *
     * @param key  根据txid获取一条记录
     * @return 二维List 取出第一维即可
     */
    public static List<List<String>> checkByTxid(String key) {
        List<List<String>> res = new ArrayList<>();

        String sql = "select * from `zcash` where `txid` = ?";        //sql语句

        Connection con = _getConnection();
        int rows = -1;
        PreparedStatement statement = null;
        try {
            statement  = con.prepareStatement(sql);

            statement.setString(1, key);

            ResultSet rs = statement.executeQuery();

            System.out.println("查询成功");
            while (rs.next()) {
                String txid = rs.getString(1); // 注意：索引从1开始
                String add1 = rs.getString(2);
                String add2 = rs.getString(3);

                res.add(Arrays.asList(txid,add1,add2));
            }
        } catch (SQLException e) {
            System.out.println("操作失败");
            e.printStackTrace();
        } finally {
            Close(con, statement);
        }
        return res;
    }

    /**
     *
     * @param key 根据发送地址获取发送历史，包含一或多条记录
     * @return 二维List
     */
    public static List<List<String>> checkByAdd1(String key) {
        List<List<String>> res = new ArrayList<>();

        String sql = "select * from `zcash` where `from_address` = ?";        //sql语句

        Connection con = _getConnection();
        int rows = -1;
        PreparedStatement statement = null;
        try {
            statement  = con.prepareStatement(sql);

            statement.setString(1, key);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String txid = rs.getString(1); // 注意：索引从1开始
                String add1 = rs.getString(2);
                String add2 = rs.getString(3);

                res.add(Arrays.asList(txid,add1,add2));
            }
        } catch (SQLException e) {
            System.out.println("操作失败");
            e.printStackTrace();
        } finally {
            Close(con, statement);
        }
        return res;
    }

    /**
     *
     * @param key 根据接受地址获取接受历史，包含一或多条记录
     * @return 二维List
     */
    public static List<List<String>> checkByAdd2(String key) {
        List<List<String>> res = new ArrayList<>();

        String sql = "select * from `zcash` where `to_address` = ?";        //sql语句

        Connection con = _getConnection();
        int rows = -1;
        PreparedStatement statement = null;
        try {
            statement  = con.prepareStatement(sql);

            statement.setString(1, key);

            ResultSet rs = statement.executeQuery();

            System.out.println("查询成功");
            while (rs.next()) {
                String txid = rs.getString(1); // 注意：索引从1开始
                String add1 = rs.getString(2);
                String add2 = rs.getString(3);

                res.add(Arrays.asList(txid,add1,add2));
            }
        } catch (SQLException e) {
            System.out.println("操作失败");
            e.printStackTrace();
        } finally {
            Close(con, statement);
        }
        return res;
    }

    /**
     *
     * @param key 根据发送地址获取发送历史的所有txid
     * @return 一维List，全为txid
     */
    public static List<String> checkByAdd1_txid(String key){
        List<String> res = new ArrayList<>();
        List<List<String>> res_all = checkByAdd1(key);
        for (int i=0; i<res_all.size();i++){
            res.add(res_all.get(i).get(0));
        }
        return res;
    }

    /**
     *
     * @param key 根据主键删除一条记录
     * @return 一维List，全为txid
     */
    public static int delete(String key) {

        String sql = "delete from `zcash` where `txid` = ?";        //sql语句

        Connection con = _getConnection();
        int rows = -1;
        PreparedStatement statement = null;
        try {
            statement  = con.prepareStatement(sql);

            statement.setString(1, key);

            rows = statement.executeUpdate();        //执行sql语句
            System.out.println("删除成功");
        } catch (SQLException e) {
            System.out.println("操作失败");
            e.printStackTrace();
        } finally {
            Close(con, statement);
        }
        return rows;
    }

    /**
     *
     * @param params 插入一条记录   e.g. Object[] params = {"test_txid1","test_add1","test_add3"};
     * @return
     */
    public static int insert(Object[] params)  {
        String sql = "insert into `zcash` (`txid`,`from_address`,`to_address`) value(?,?,?)";

        Connection con = _getConnection();

        PreparedStatement statement = null;
        int rows = -1;
        if (con == null) {
            return rows;
        }
        try {
            con.setAutoCommit(false);
            statement = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            rows = statement.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            Close(con, statement);
        }
        return rows;
    }

    public static void Close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 关闭statement连接
     *
     * @param statement 连接
     */
    public static void Close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 关闭connection和statement连接
     *
     * @param connection 连接
     */
    public static void Close(Connection connection, Statement statement) {
        Close(statement);
        Close(connection);
    }

}
