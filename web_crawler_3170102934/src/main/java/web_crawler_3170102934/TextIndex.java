package web_crawler_3170102934;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.DuplicateFilterBuilder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wltea.analyzer.lucene.IKAnalyzer;
import java.sql.*;

public class TextIndex {
	public static void main(String[] args) {
		TextIndex w=new TextIndex();
		String filePath="data/index";//创建索引的存储目录
		w.createIndex(filePath);//创建索引
		System.out.println("Index created!");
	}
	public void createIndex(String filePath){
		File f=new File(filePath);
		IndexWriter iwr=null;
		try {
			Directory dir=FSDirectory.open(f);
			Analyzer analyzer = new IKAnalyzer();
			IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_4_10_0,analyzer);
			iwr=new IndexWriter(dir,conf);//建立IndexWriter。固定套路
			Connection c = SQLiteJDBC.ConnectToDB();
			Statement stmt = null;
			stmt = c.createStatement();
			String sql = "select * from Books";
		    ResultSet rs = stmt.executeQuery(sql);
		    while(rs.next()) {
		    	BookInfo book = new BookInfo(rs.getString("URL"),
		    			rs.getString("TITLE"),
		    			rs.getString("AUTHOR"),
		    			rs.getString("CLASSIFY"),
		    			rs.getString("PUBLISHER"),
		    			rs.getString("IMG"),
		    			rs.getString("PRICE"),
		    			rs.getString("RECOMMEND"),
		    			rs.getString("BOOK_INTRO"),
		    			rs.getString("AUTHOR_INTRO"),
		    			rs.getString("CONTENT"));
		    	Document doc=getDocument(book);
		    	iwr.addDocument(doc);
		    }
		    c.close();
//			String filename = "data/Books.json";
//			JSONArray jsonArray = JSONUtil.parseJSONFile(filename);
//			for(int i = 0;i<jsonArray.length();i++) {
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				bookInfo book = new bookInfo(jsonObject.getString("URL"),
//					 						 jsonObject.getString("TITLE"),
//								    		jsonObject.getString("AUTHOR"),
//								    		jsonObject.getString("CLASSIFY"),
//								    		jsonObject.getString("PUBLISHER"),
//								    		jsonObject.getString("IMG"),
//								    		jsonObject.getString("PRICE"),
//								    		jsonObject.getString("RECOMMEND"),
//								    		jsonObject.getString("BOOK_INTRO"),
//								    		jsonObject.getString("AUTHOR_INTRO"),
//								    		jsonObject.getString("CONTENT")
//											 );
//				Document doc=getDocument(book);
//				iwr.addDocument(doc);//添加doc，Lucene的检索是以document为基本单位
//			}
		    //reader.close();
			iwr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Document getDocument(BookInfo book){
		//doc中内容由field构成，在检索过程中，Lucene会按照指定的Field依次搜索每个document的该项field是否符合要求。
		Document doc=new Document();
		Field f0=new TextField("Key",book.getUrl(), Field.Store.YES);
		Field f1=new TextField("title",book.getTitle(),Field.Store.YES);
		Field f2=new TextField("author",book.getAuthor(),Field.Store.YES);
		Field f3=new TextField("classify",book.getClassify(),Field.Store.YES);
		Field f4=new TextField("publisher",book.getPublisher(),Field.Store.YES);
		Field f5=new TextField("price",book.getPrice(),Field.Store.YES);
		Field f6=new TextField("img_src",book.getImg_src(),Field.Store.YES);
		Field f7=new TextField("recommend",book.getRecommend(),Field.Store.YES);
		Field f8=new TextField("book_intro",book.getBook_intro(),Field.Store.YES);
		Field f9=new TextField("author_intro",book.getAuthor_intro(),Field.Store.YES);
		Field f10=new TextField("content",book.getContent(),Field.Store.YES);
		
		doc.add(f0);
		doc.add(f1);
		doc.add(f2);
		doc.add(f3);
		doc.add(f4);
		doc.add(f5);
		doc.add(f6);
		doc.add(f7);
		doc.add(f8);
		doc.add(f9);
		doc.add(f10);
		
		return doc;
		
	}
	
	public void search(String filePath, String queryStr, String index){
		File f=new File(filePath);
		try {
			IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(FSDirectory.open(f)));
			Analyzer analyzer = new IKAnalyzer();
			//指定field为“name”，Lucene会按照关键词搜索每个doc中的name。
			QueryParser parser = new QueryParser(Version.LUCENE_4_10_0, index, analyzer);
			
			Query query=parser.parse(queryStr);
			HashSet<String> Keys = new HashSet();
			TopDocs hits=searcher.search(query,10);//前面几行代码也是固定套路，使用时直接改field和关键词即可
			System.out.println("查询结果的总条数："+ hits.totalHits);
			int count = 1;
			for(ScoreDoc doc:hits.scoreDocs){
				Document d=searcher.doc(doc.doc);
				//System.out.println(d.get("Key"));
				if(Keys.contains(d.get("Key"))) continue;
				Keys.add(d.get("Key"));
				System.out.println(count+".");
				System.out.println("书名："+d.get("title"));
				System.out.println("作者："+d.get("author"));
				System.out.print("分类："+d.get("classify"));
				System.out.println("出版社："+d.get("publisher"));
				System.out.println("价格："+d.get("price"));
				System.out.println("图片链接："+d.get("img_src"));
				System.out.println("编辑推荐：\n"+d.get("recommend"));
				System.out.println("内容简介:\n"+d.get("book_intro"));
				System.out.println("作者简介:\n"+d.get("author_intro"));
				System.out.println("目录:\n"+d.get("content"));
				
				count++;
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
