package cn.itcast.bookstore.user.service;

import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;

//User业务层
public class UserService {
	private UserDao userDao=new UserDao();
	
	//注册
	public void regist(User form) throws UserException {
		//校验用户名
		User user=userDao.findByUsername(form.getUsername());
		if(user != null) throw new UserException("用户名已注册");
		
		//校验email
		user=userDao.findByEmail(form.getEmail());
		if(user != null) throw new UserException("email已注册");
		
		//添加用户到数据库
		userDao.add(form);
	}
	
	//激活
	public void active(String code) throws UserException {
		
		User user=userDao.findByUid(code);
		if(user == null) throw new UserException("激活码无效");
		
		if(user.isState()) throw new UserException("您已激活,不要再激活了,除非你想死");
		
		userDao.updateState(user.getUid(),true);
	}
	
	//登录
	public User login(User form) throws UserException {
		User user=userDao.findByUsername(form.getUsername());
		if(user == null) throw new UserException("用户名不存在");
		if(!user.getPassword().equals(form.getPassword()))
			throw new UserException("密码错误");
		if(!user.isState()) throw new UserException("尚未激活");
		return user;
	}
}
