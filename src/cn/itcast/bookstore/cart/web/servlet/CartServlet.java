package cn.itcast.bookstore.cart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.servlet.BaseServlet;

public class CartServlet extends BaseServlet {
	//添加购物车条目
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//1.得到购物车
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		
		//2.通过表单数据,得到条目(图书和数目)
		String bid=request.getParameter("bid");
		int count=Integer.parseInt(request.getParameter("count"));
		Book book=new BookService().load(bid);
		CartItem cartItem=new CartItem();
		cartItem.setBook(book);
		cartItem.setCount(count);
		
		//把得到的条目添加到购物车中
		cart.add(cartItem);
		return "f:/jsps/cart/list.jsp";
	}
	
	//清空购物车条目
	public String clear(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	//删除购物车条目
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		String bid=request.getParameter("bid");
		cart.delete(bid);
		return "f:/jsps/cart/list.jsp";	
	}
	
}
