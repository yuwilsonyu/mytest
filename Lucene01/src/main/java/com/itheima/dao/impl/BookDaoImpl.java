package com.itheima.dao.impl;

import com.itheima.dao.BookDao;
import com.itheima.entity.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    Connection connection = null;
    PreparedStatement psmt = null;
    ResultSet rs = null;

    public List<Book> findAllBook() {
        List<Book> bookList = new ArrayList<Book>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lucene","root","root");
            String sql = "SELECT * FROM book";
            psmt = connection.prepareStatement(sql);
            rs = psmt.executeQuery();
            while(rs.next()){
                Book book = new Book();
                book.setId(rs.getInt("id"));
                // 图书名称
                book.setBookname(rs.getString("bookname"));
                // 图书价格
                book.setPrice(rs.getFloat("price"));
                // 图书图片
                book.setPic(rs.getString("pic"));
                // 图书描述
                book.setBookdesc(rs.getString("bookdesc"));
                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (rs !=null) rs.close();
                if(psmt !=null) psmt.close();
                if(connection !=null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return bookList;
    }
}
