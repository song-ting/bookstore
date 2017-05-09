package cn.itcast.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * 查询所有图书
	 * 
	 * @return
	 */
	public List<Book> findAll() {
		try {
			String sql = "select * from book where del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按分类查询
	 * 
	 * @param cid
	 * @return
	 */
	public List<Book> findByCategory(String cid) {
		try {
			String sql = "select * from book where cid=? and del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class), cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 加载点击的图书
	 * 
	 * @param bid
	 * @return
	 */
	public Book findByBid(String bid) {
		try {
			// 需要在Book对象中保存Category对象的信息(cid)
			String sql = "select * from book where bid=?";
			Map<String, Object> map = qr.query(sql, new MapHandler(), bid);

			// 方法1:使用一个Map,得到两个对象(Book Category),并建立关系(合并为一个Book)
			// 优点:快捷 缺点:Book对象中的Category没有cname
			/*
			 * Category category=CommonUtils.toBean(map, Category.class); Book
			 * book=CommonUtils.toBean(map, Book.class);
			 * book.setCategory(category); return book;
			 */

			// 方法2:通过map中的cid得到Category对象,并将Book和Category这两个对象建立关系(合并为一个Book)
			// 优点:Book对象中的Category数据成员齐全 缺点:麻烦
			// Book book=qr.query(sql, new BeanHandler<Book>(Book.class), bid);
			Book book = CommonUtils.toBean(map, Book.class);
			String cid = (String) map.get("cid");
			String sql1 = "select * from category where cid=?";
			Category category = qr.query(sql1, new BeanHandler<Category>(
					Category.class), cid);
			book.setCategory(category);
			return book;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询指定分类下的图书数目
	 * 
	 * @param cid
	 * @return
	 */
	public int getCountByCid(String cid) {
		try {
			String sql = "select count(*) from book where cid=?";
			Number count = (Number) qr.query(sql, new ScalarHandler(), cid);
			return count.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加图书
	 * 
	 * @param book
	 */
	public void add(Book book) {
		try {
			String sql = "insert into book values(?,?,?,?,?,?,?)";
			Object[] params = { book.getBid(), book.getBname(),
					book.getPrice(), book.getAuthor(), book.getImage(),
					book.getCategory().getCid(), false };
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除图书
	 * 
	 * @param bid
	 */
	public void delete(String bid) {
		try {
			String sql = "update book set del=true where bid=?";
			qr.update(sql, bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 编辑图书
	 * 
	 * @param book
	 */
	public void edit(Book book) {
		try {
			String sql = "update book set bname=?, price=?, author=?, cid=? where bid=?";
			Object[] params = { book.getBname(), book.getPrice(),
					book.getAuthor(), book.getCategory().getCid(),
					book.getBid() };
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
