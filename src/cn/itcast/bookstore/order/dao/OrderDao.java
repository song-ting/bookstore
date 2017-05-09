package cn.itcast.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.sun.org.apache.bcel.internal.generic.NEW;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * 添加订单
	 */
	public void addOrder(Order order) {
		try {
			String sql = "insert into orders values(?,?,?,?,?,?)";
			// 处理util的Date转换成sql的Timestamp
			Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
			Object[] params = { order.getOid(), timestamp, order.getTotal(),
					order.getState(), order.getOwner().getUid(),
					order.getAddress() };
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加订单条目
	 */
	public void addOrderItemList(List<OrderItem> orderItemList) {
		try {
			String sql = "insert into orderitem values(?,?,?,?,?)";
			/*
			 * QueryRunner的batch方法包含两个参数sql,object[][] params
			 */
			Object[][] params = new Object[orderItemList.size()][];
			// 循环遍历orderItemList,使用每一个orderItem对象为params中每个一维数组赋值
			for (int i = 0; i < orderItemList.size(); i++) {
				OrderItem item = orderItemList.get(i);
				params[i] = new Object[] { item.getIid(), item.getCount(),
						item.getSubtotal(), item.getOrder().getOid(),
						item.getBook().getBid() };
			}
			qr.batch(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按uid查询订单
	 */
	public List<Order> findByUid(String uid) {

		try {
			// 1.通过uid查出当前用户所有的List<Order>
			String sql = "select * from orders where uid=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(
					Order.class), uid);

			// 2.循环遍历每个Order,为其加载它所有的订单条目
			for (Order order : orderList) {
				loadOrderItems(order);
			}

			// 3.返回订单列表
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 加载指定的订单所有的订单条目
	 * 
	 * @throws SQLException
	 */
	private void loadOrderItems(Order order) throws SQLException {
		// 查询两张表:orderItem book
		String sql = "select * from orderItem i,book b where i.bid=b.bid and oid=?";
		// 因为一行结果集对应的不在是一个javaBean,所以不能使用BeanListHandler,而是MapListHandler
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(),
				order.getOid());
		// mapList是多个map,每个map对应一行结果集
		// 使用一个Map生成两个对象:OrderItem Book,然后再建立两者的关系(把Book设置给OrderItem)
		// 循环遍历每个map,使用map生成两个对象,然后建立关系,最终为一个OderItem,并把OderItem保存起来

		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}

	/**
	 * 把mapList中每个Map转换成两个对象(OrderItem,Book),并建立关系
	 * 
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (Map<String, Object> map : mapList) {
			OrderItem item = toOrderItem(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}

	/**
	 * 把一个Map(OrderItem,Book)转换成一个OrderItem对象
	 * 
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	/**
	 * 加载订单
	 * 
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		try {
			String sql = "select * from orders where oid=?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class),
					oid);

			// 为order加载它的所有条目
			loadOrderItems(order);

			// 返回订单(订单列表)
			return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 通过oid查询订单状态
	 */
	public int getStateByOid(String oid) {
		try {
			String sql = "select state from orders where oid=?";
			Number number = (Number) qr.query(sql, new ScalarHandler(), oid);
			return number.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改订单状态
	 */
	public void updateState(String oid, int state) {
		try {
			String sql = "update orders set state=? where oid=?";
			qr.update(sql, state, oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询所有订单
	 * 
	 * @return
	 */
	public List<Order> findAll() {
		try {
			String sql = "select * from orders";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(
					Order.class));
			for (Order order : orderList) {
				loadOrderItems(order);
			}

			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询指定状态的订单
	 * 
	 * @param i
	 * @return
	 */
	public List<Order> findByState(int i) {
		try {
			String sql = "select * from orders where state=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(
					Order.class), i);
			for (Order order : orderList) {
				loadOrderItems(order);
			}

			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
