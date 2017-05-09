package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;

import cn.itcast.bookstore.book.domain.Book;

//购物车条目
public class CartItem {
	private Book book;
	private int count;
		
	//小计方法
	//处理了计算误差
	public double getSubtotal() {
		BigDecimal b1=new BigDecimal(book.getPrice()+"");
		BigDecimal b2=new BigDecimal(count+"");
		return b1.multiply(b2).doubleValue();
	}
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
