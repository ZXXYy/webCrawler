package web_crawler_3170102934;

import java.sql.*;

public class SQLiteJDBC
{
  public static void main( String args[] )
  {
    Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:/Users/zhengxiaoye/Desktop/Java应用技术/homework/homework4/dangdangBook.db");
      c.setAutoCommit(false);
      System.out.println("Opened database successfully");
//      stmt = c.createStatement();
//      String sql = "INSERT INTO Books (URL,TITLE,AUTHOR,CLASSIFY,PUBLISHER, IMG,"+
//    		  		"RECOMMEND, BOOK_INTRO, AUTHOR_INTRO, CONTENT, PRICE) " +
//                   "VALUES ('http://product.dangdang.com/29130983.html',"+
//                   " '梦溪笔谈','沈复', '图书>文学>中国古代随笔', '人民文学', 'http://img3m2.ddimg.cn/0/27/28473192-1_w_3.jp', "
//                   + "'暂无','暂无','暂无','暂无','¥80' );";
      //stmt.executeUpdate(sql);
      stmt = c.createStatement();
      ResultSet rset = stmt.executeQuery("select * from Books where URL=='http://product.dangdang.com/29157747.html'"); 
      int rowCount = 0; 
      while (rset.next()) { 
        rowCount++; 
      }
      System.out.println(rowCount);
      stmt.close();
      c.commit();
      c.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    System.out.println("Table created successfully");
  }
  public static Connection ConnectToDB() {
	  Connection c = null;
	  try {
		  Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:/Users/zhengxiaoye/Desktop/Java应用技术/homework/homework4/dangdangBook.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	  }catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	  }
	  return c;
  }
  
  public static void Insert(Connection c, bookInfo book) {
	  Statement stmt = null;
	  try {
		stmt = c.createStatement();
		ResultSet rset = stmt.executeQuery("select * from Books_new where ID=="+book.hashCode()); 
	    int rowCount = 0; 
	    while (rset.next()) { 
	        rowCount++; 
	    }
		if(rowCount>0) return;
		String sql = "INSERT INTO Books_new (ID, URL,TITLE,AUTHOR,CLASSIFY,PUBLISHER, IMG,"+
		  		"RECOMMEND, BOOK_INTRO, AUTHOR_INTRO, CONTENT, PRICE) " +
               "VALUES ('"+book.hashCode()+"','"+
		  				  book.getUrl()+"','"+
		  				  book.getTitle()+"','"+
		  				  book.getAuthor()+"','"+
		  				  book.getClassify()+"','"+
		  				  book.getPublisher()+"','"+
		  				  book.getImg_src()+"','"+
		  				  book.getRecommend()+"','"+
		  				  book.getBook_intro()+"','"+
			  			  book.getAuthor_intro()+"','"+
			  			  book.getContent()+"','"+
			  			  book.getPrice()+"');";
		
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
		c.close();
		System.out.println("Insert database successfully");
	} catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      e.printStackTrace();
	      //System.exit(0);
	  }
	  
  }
  
}