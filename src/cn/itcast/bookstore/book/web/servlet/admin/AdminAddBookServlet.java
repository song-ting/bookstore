package cn.itcast.bookstore.book.web.servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/*
		 * 把表单数据封装到Book对象中 上传三步
		 */
		// 1.创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory(15 * 1024,
				new File("F:/temp"));
		// 2.得到解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		// 设置单个文件大小限制为50KB
		sfu.setFileSizeMax(50 * 1024);
		// 3.使用解析器去解析request对象,得到List<FileItem>
		try {
			List<FileItem> fileItemList = sfu.parseRequest(request);
			/*
			 * 1.把fileItemList中的表单领域数据封装到Book对象中 *把表单领域数据封装到map中
			 * *把map中的数据封装到Book对象中
			 */
			Map<String, String> map = new HashMap<String, String>();
			for (FileItem fileItem : fileItemList) {
				if (fileItem.isFormField()) {// 判断表单领域数据
					map.put(fileItem.getFieldName(),
							fileItem.getString("UTF-8"));
				}
			}
			Book book = CommonUtils.toBean(map, Book.class);
			// 为book设置bid
			book.setBid(CommonUtils.uuid());
			// 先把map中的cid封装到Category中,再把Category封装到Book中
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			/*
			 * 2.保存上传的文件 *保存的目录 *保存的文件名
			 */
			// 得到保存的目录
			String savePath = this.getServletContext().getRealPath("/book_img");
			// 得到保存的文件名:给原来的文件名加上uuid作前缀 避免文件名冲突
			String filename = CommonUtils.uuid() + "_"
					+ fileItemList.get(1).getName();

			// 校验文件的拓展名
			if (!filename.toLowerCase().endsWith("jpg")
					&& !filename.toLowerCase().endsWith("jpeg")) {
				request.setAttribute("msg", "上传的图片不是jpg(jpeg)格式");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}

			File destFile = new File(savePath, filename);
			// 保存上传文件到目标文件位置
			fileItemList.get(1).write(destFile);

			/*
			 * 3.设置Book对象的image成员值(图片的相对路径)
			 */
			book.setImage("book_img/" + filename);

			/*
			 * 4.使用bookService的方法完成保存
			 */
			bookService.add(book);

			// 校验图片的尺寸
			Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
			if (image.getWidth(null) > 200 || image.getHeight(null) > 200) {
				destFile.delete();// 删除这个文件！
				request.setAttribute("msg", "您上传的图片尺寸超出了200 * 200！");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}

			// 5.返回到图书列表
			request.getRequestDispatcher(
					"/admin/AdminBookServlet?method=findAll").forward(request,
					response);
		} catch (Exception e) {
			if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
				request.setAttribute("msg", "上传的图片大小超出了50KB");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
			}
		}

	}

}
