package cn.itcast.bookstore.category.web.servlet.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService=new CategoryService();
	
	/**
	 * 修改分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Category category=CommonUtils.toBean(request.getParameterMap(), Category.class);
		categoryService.edit(category);
		return findAll(request,response);
	}
	
	/**
	 * 修改分类之前的加载分类方法
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editPre(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String cid=request.getParameter("cid");
		Category category=categoryService.load(cid);
		request.setAttribute("category", category);
		return "f:/adminjsps/admin/category/mod.jsp";
	}
	
	/**
	 * 删除分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//1.获取cid参数
		String cid=request.getParameter("cid");
		//2.调用service方法,传递cid参数
		try {
			categoryService.delete(cid);
		} catch (CategoryException e){
			//如果出现异常,转发异常信息到/adminjsps/msg.jsp
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
		//3.调用findAll()方法		
		return findAll(request, response);
	}
	
	/**
	 * 查询所有分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Category> categoryList=categoryService.findAll();
		request.setAttribute("categoryList",categoryList);
		return "f:/adminjsps/admin/category/list.jsp";
	}
	/**
	 * 添加分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//1.封装表单数据
		Category category=CommonUtils.toBean(request.getParameterMap(), Category.class);
		//2.补全cid
		category.setCid(CommonUtils.uuid());
		//3.调用Service方法完成添加工作
		categoryService.add(category);
		//添加完成,调用findAll()方法
		return findAll(request, response);
	}
}
