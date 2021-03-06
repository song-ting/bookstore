package cn.itcast.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

//User持久层
public class UserDao {
	private QueryRunner qr=new TxQueryRunner();
	
	//按用户名查询
	public User findByUsername(String username) {
		try {
			String sql="select * from tb_user where username=?";
			return qr.query(sql,new BeanHandler<User>(User.class),username);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//按email查询
	public User findByEmail(String email) {
		try {
			String sql="select * from tb_user where email=?";
			return qr.query(sql,new BeanHandler<User>(User.class),email);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//添加用户
	public void add(User user) {
		try {
			String sql="insert into tb_user values(?,?,?,?,?,?)";
			Object[] params={user.getUid(),user.getUsername(),user.getPassword(),
					user.getEmail(),user.getCode(),user.isState()};
			qr.update(sql,params);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
		
	//按code查询
	public User findByUid(String code) {
		try {
			String sql="select * from tb_user where code=?";
			return qr.query(sql,new BeanHandler<User>(User.class),code);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//修改指定用戶的指定状态
	public void updateState(String uid,boolean state) {
		try {
			String sql="update tb_user set state=? where uid=?";
			qr.update(sql,state,uid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
