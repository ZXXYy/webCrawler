package web_crawler_3170102934;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;


public class SQLiteJDBC
{
  
  public static Connection ConnectToDB() {
	  Connection c = null;
	  try {
		  Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:data/dangdangBook.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	  }catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	  }
	  return c;
  }
  public static void Insert(Connection c, BookInfo book) {
	  Statement stmt = null;
	  try {
		stmt = c.createStatement();
		ResultSet rset = stmt.executeQuery("select * from Books where ID=="+book.hashCode()); 
	    int rowCount = 0; 
	    while (rset.next()) { 
	        rowCount++; 
	    }
		if(rowCount>0) return;
		String sql = "INSERT INTO Books (ID, URL,TITLE,AUTHOR,CLASSIFY,PUBLISHER, IMG,"+
		  		"RECOMMEND, BOOK_INTRO, AUTHOR_INTRO, CONTENT, PRICE, REAL_IMG) " +
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
			  			  book.getPrice()+"',"+
			  			  "?);";
		
		PreparedStatement ps = null;
  	  	ps = c.prepareStatement(sql);
  	  	ps.setBytes(1, readFile(book.getImg_src()));
	  	int temp = ps.executeUpdate();
		if(temp>0) {
			System.out.println("Insert database successfully");
		}
		else {
			System.out.println("Insert database failed！");
		}
		c.commit();
		c.close();
	} catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      e.printStackTrace();
	  }
	  
  }
  public static byte[] readFile(String imgUrl) throws IOException {
	  URL url = new URL(imgUrl);
	  URLConnection connection = url.openConnection();
	  connection.setConnectTimeout(10 * 1000);
	  InputStream in = connection.getInputStream();
      ByteArrayOutputStream bos = null;
      try {
          byte[] buffer = new byte[1024];
          bos = new ByteArrayOutputStream();
          for (int len; (len = in.read(buffer)) != -1;) {
              bos.write(buffer, 0, len);
          }
      } catch (FileNotFoundException e) {
          System.err.println(e.getMessage());
      } catch (IOException e2) {
          System.err.println(e2.getMessage());
      }
      return bos != null ? bos.toByteArray() : null;
  }
}