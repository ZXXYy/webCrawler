package web_crawler_3170102934;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.sql.*;
import java.sql.*;
public class BasicCrawler extends WebCrawler {

    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    
    private static int book_index = 0;

    private final AtomicInteger numSeenImages;
    
    private final Connection SQLiteJDBCService;

    /**
     * Creates a new crawler instance.
     *
     * @param numSeenImages This is just an example to demonstrate how you can pass objects to crawlers. In this
     * example, we pass an AtomicInteger to all crawlers and they increment it whenever they see a url which points
     * to an image.
     */
    public BasicCrawler(AtomicInteger numSeenImages, Connection SQLiteJDBCService) {
        this.numSeenImages = numSeenImages;
        this.SQLiteJDBCService = SQLiteJDBCService;
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            numSeenImages.incrementAndGet();
            return false;
        }

        // Only accept the url if it is in the "http://product.dangdang.com" or "http://book.dangdang.com" domain
        // and protocol is "http".
        return href.startsWith("http://book.dangdang.com") || 
        	 href.startsWith("http://product.dangdang.com") ;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        BookInfo book = new BookInfo();
        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);

        if (page.getParseData() instanceof HtmlParseData && url.startsWith("http://product.dangdang.com")) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            // parse the html
            Document document = Jsoup.parse(html);
            
            // get title
            Element title_ele = document.getElementsByTag("h1").first();
            String title = title_ele.attr("title");
            
            // get author and publisher
            String author = null;
            String publish = null;
            Element messbox = document.getElementsByClass("messbox_info").first();
            Elements infos = messbox.getElementsByAttribute("dd_name");
            for(Element info:infos) {
            	if(info.attr("id").equals("author"))
            		author = info.text().substring(3);
            	else if(info.attr("dd_name").equals("出版社"))
            		publish = info.text();
            }
            
            // get price
            Element price_ele = document.getElementById("dd-price");
            String price = price_ele.text();
            
            // get classification info
            Element class_ele = document.getElementById("detail-category-path");
            Elements class_cols = class_ele.getElementsByClass("lie");
            StringBuffer classify = new StringBuffer();
            for(Element class_col:class_cols) {
            	classify.append(class_col.text()+"\n");
            }
            
            // get img URL
            Element img_ele = document.getElementById("largePicDiv");
            Element img = img_ele.getElementById("largePic");
            String img_src = img.attr("src");
            
            String[] actualURL = url.split("html");
            book.setUrl(actualURL[0]+"html");
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublisher(publish);
            book.setPrice(price);
            book.setClassify(new String(classify));
            book.setImg_src(img_src);
            
            // generate the ajax URL
            StringBuffer request_url=new StringBuffer("http://product.dangdang.com/index.php?r=callback%2Fdetail&");
            Elements js = document.getElementsByTag("script").eq(1);
            
            for (Element element : js) {
    			String[] data = element.data().toString().split("var"); /*取得JS变量数组*/
    			for(String variable : data){	    					/*取得单个JS变量*/
    				if(variable.contains("=")){							/*过滤variable为空的数据*/
    					if(variable.contains("prodSpuInfo")){			/*取到满足条件的JS变量*/
    						String[]  kvp = variable.split("=");		//将字符串转json对象
							try {
								JSONObject jsonObject = new JSONObject (kvp[1]);
								String id = jsonObject.getString("productId");
	    				        String templateType = jsonObject.getString("template");
	    				        String describeMap = jsonObject.getString("describeMap");
	    				        String shopId = jsonObject.getString("shopId");
	    				        String categoryPath = jsonObject.getString("categoryPath");
	    				        request_url.append("productId="+id+"&templateType="+templateType+
	    				            		   	   "&describeMap="+describeMap+"&shopId="+shopId+
	    				            		   	   "&categoryPath="+categoryPath);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					}
    				}
    			}
    		}
            
            
         // get the ajax achieved info
            try {
            	org.jsoup.Connection conn = Jsoup.connect(new String(request_url));
				conn.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
            	Response response = conn.execute();
            	int statusCode = response.statusCode();
            	int count = 0;
            	// if fail, try again
            	while(statusCode!=200 & count<=10) {
            		conn = Jsoup.connect(new String(request_url));
    				conn.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
                	response = conn.execute();
            		count++;
            	}
            	Document doc = conn.get();
            	
            	//get the relative html info
            	String t = unicodeToString(doc.text());
				JSONObject jsonObject = new JSONObject (t);
				jsonObject = new JSONObject(jsonObject.getString("data"));
				t = jsonObject.getString("html");						
				
				Set<String> details = new HashSet<String>();
	            details.add("产品特色");
	            details.add("编辑推荐");
	            details.add("内容简介");
	            details.add("作者简介");
	            details.add("媒体评论");
	            details.add("目　　录");
	            details.add("免费在线读");
	            Set<String> extracted = new HashSet<String>();
	            extracted.add("编辑推荐");
	            extracted.add("内容简介");
	            extracted.add("作者简介");
	            extracted.add("目　　录");
	            // use the map to store data
	            Map<String, String> map = new HashMap<String,String>(); 
	            
				String[] data = t.split("[</a-z>\t\r\n ]+");
				StringBuffer temp_data = new StringBuffer();
				Set<String> hasType = new HashSet();
				String type = null;
				boolean flag = false;
				// 从html中获得编辑推荐，内容简介，作者简介，目　　录的信息
				for(String p:data) {
					if(details.contains(p) && extracted.contains(p)) {
						if(flag) {
							hasType.add(type);
							map.put(type, new String(temp_data));
						}
						// the new target info
						flag = true;
						type = p;
						temp_data.delete(0,temp_data.length());
					}
					else if(details.contains(p)) {
						if(flag) {
							hasType.add(type);
							map.put(type, new String(temp_data));
						}
						flag = false;
						temp_data.delete(0,temp_data.length());
					}
					else if(flag){
						temp_data.append(p+"\n");
					}
				}
				
				// 将筛选出来的信息存入到bookInfo中
				for(String temp: extracted) {
					if(!hasType.contains(temp)) {
						map.put(temp, "暂无");
					}
					if(temp.equals("编辑推荐")) {
						book.setRecommend(map.get(temp));
					}
					else if(temp.equals("内容简介")) {
						book.setBook_intro(map.get(temp));
					}
					else if(temp.equals("作者简介")) {
						book.setAuthor_intro(map.get(temp));
					}
					else if(temp.equals("目　　录")) {
						book.setContent(map.get(temp));
					}
				}
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // 保存爬下来的数据到数据库中
            logger.debug("======Database insertion======");
			Connection c = SQLiteJDBC.ConnectToDB();
			SQLiteJDBC.Insert(c, book);
			logger.debug("======Database insertion finished!======");
        }

        // 输出爬虫的返回头信息
        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
            logger.debug("Response headers:");
            for (Header header : responseHeaders) {
                logger.debug("\t{}: {}", header.getName(), header.getValue());
            }
        }
        logger.debug("=============");
    }

    
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }
}