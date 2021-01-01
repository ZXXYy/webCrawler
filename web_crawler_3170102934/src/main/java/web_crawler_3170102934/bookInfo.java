package web_crawler_3170102934;

import java.io.InputStream;

public class bookInfo {
	private String title;
	private String author;
	private String classify;
	private String publisher;
	private String img_src;
	private String recommend;
	private String book_intro;
	private String author_intro;
	private String content;
	private String price;
	private String url;

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	public bookInfo() 
	{
	}
	public bookInfo(String url, String title, String author,String classify,String publisher, 
			 		 String img_src, String price,String recommend,String book_intro,String author_intro,String content)
	{
		this.url = url;
		this.title = title;
		this.author = author;
		this.classify = classify;
		this.publisher = publisher;
		this.img_src = img_src;
		this.price = price;
		this.recommend = recommend;
		this.book_intro = book_intro;
		this.author_intro = author_intro;
		this.content = content;

	}
	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		return author;
	}
	public String getClassify() {
		return classify;
	}
	public String getPublisher() {
		return publisher;
	}
	public String getImg_src() {
		return img_src;
	}
	public String getRecommend() {
		return recommend;
	}
	public String getBook_intro() {
		return book_intro;
	}
	public String getAuthor_intro() {
		return author_intro;
	}
	public String getContent() {
		return content;
	}
	public String getPrice() {
		return price;
	}
	public String getUrl() {
		return url;
	}
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public void setImg_src(String img_src) {
		this.img_src = img_src;
	}
	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}
	public void setBook_intro(String book_intro) {
		this.book_intro = book_intro;
	}
	public void setAuthor_intro(String author_intro) {
		this.author_intro = author_intro;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
