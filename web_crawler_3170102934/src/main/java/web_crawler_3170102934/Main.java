package web_crawler_3170102934;

import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		text_index w=new text_index();
		String filePath="data/index";//创建索引的存储目录
		w.createIndex(filePath);//创建索引
		Scanner s = new Scanner(System.in);
		System.out.println("欢迎使用图书搜索引擎！");
		while(true) {
			System.out.println("1. 开始检索");
			System.out.println("2. 退出");
			System.out.println("请选择：");
			int choice = s.nextInt();
			if(choice==2) break;
			display();
			choice = s.nextInt();
			s.nextLine();
			String index = null;
			switch(choice) {
				case 1:
					index = "title";
					break;
				case 2:
					index = "author";
					break;
				case 3:
					index = "classify";
					break;
				case 4:
					index = "publisher";
					break;
			}
			System.out.println("请输入您想要检索的内容：");
			String queryStr = s.nextLine();
			w.search(filePath,queryStr,index);//搜索
		}
		System.out.println("正在退出...");
		System.out.println("感谢使用！");
	}
	
	public static void display() {
		 System.out.println("请选择您所要检索的依据：");
		 System.out.println("1. 标题");
		 System.out.println("2. 作者");
		 System.out.println("3. 分类");
		 System.out.println("4. 出版社");
		 System.out.println("请选择：");
	 }
}
