package cn.itcast.bookstore.order.web.servlet.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.service.OrderService;
import cn.itcast.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet {
	OrderService orderService=new OrderService();
	
	/**
	 * 查询所有订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Order> orderList=orderService.findAll();
		
		request.setAttribute("orderList", orderList);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 查询指定状态的订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByState(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String statestr=request.getParameter("state");
		int stateint=Integer.parseInt(statestr);
		List<Order> orderList=orderService.findByState(stateint);
		request.setAttribute("orderList", orderList);
		return "f:/adminjsps/admin/order/list.jsp";
	}
}
