package cn.itcast.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.UserException;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;

//User表述层
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	// 退出当前账户
	public String quit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "r:/index.jsp";
	}

	// 登录功能
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		Map<String, String> errors = new HashMap<String, String>();

		String username = form.getUsername();
		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "请输入用户名");
		}

		String password = form.getPassword();
		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "请输入密码");
		}

		if (errors.size() > 0) {
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}

		try {
			User user = userService.login(form);
			request.getSession().setAttribute("session_user", user);
			// 用户一登录就发车
			request.getSession().setAttribute("cart", new Cart());
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}
	}

	// 激活功能
	public String active(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			userService.active(request.getParameter("code"));
			request.setAttribute("msg", "你已激活,请马上登录");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}

	// 注册功能
	public String regist(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 封装表单数据
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		// 补全:uid code(激活码)
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		// 输入校验
		// 1.创建一个Map,用来封装错误信息,key为表单字段名,value为错误信息
		Map<String, String> errors = new HashMap<String, String>();
		// 2.获取form中的username password email并校验
		String username = form.getUsername();
		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "用户名不能为空");
		} else if (username.length() < 3 || username.length() > 10) {
			errors.put("username", "用户名长度必须在3-10之间");
		}

		String password = form.getPassword();
		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "密码不能为空");
		} else if (password.length() < 3 || password.length() > 10) {
			errors.put("password", "密码长度必须在3-10之间");
		}

		String email = form.getEmail();
		if (email == null || email.trim().isEmpty()) {
			errors.put("email", "email不能为空");
		} else if (!email.matches("\\w+@\\w+\\.\\w+")) {
			errors.put("email", "email格式错误");
		}
		// 3.检查错误信息
		if (errors.size() > 0) {
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}

		try {
			userService.regist(form);

		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		// 发邮件,获取配置文件内容
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader()
				.getResourceAsStream("email_template.properties"));
		String host = props.getProperty("host");
		String uname = props.getProperty("uname");
		String pwd = props.getProperty("pwd");
		String from = props.getProperty("from");
		String to = form.getEmail();
		String subject = props.getProperty("subject");
		String content = props.getProperty("content");
		content = MessageFormat.format(content, form.getCode());// 替换{0}占位符

		Session session = MailUtils.createSession(host, uname, pwd);
		Mail mail = new Mail(from, to, subject, content);

		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {

		}

		// 注册成功,转发msg到msg.jsp
		request.setAttribute("msg", "注册成功,请马上到邮箱激活");
		return "f:/jsps/msg.jsp";

	}
}
