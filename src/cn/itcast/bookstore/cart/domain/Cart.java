package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
	//map的key为bid
	private Map<String,CartItem> map=new LinkedHashMap<String,CartItem>();
	
	/**
	 * 获取购物车商品合计
	 */
	public double getTotal() {
		BigDecimal total=new BigDecimal("0");
		for(CartItem cartItem:map.values()) {
			BigDecimal subtotal=new BigDecimal(cartItem.getSubtotal()+"");
			total =total.add(subtotal);
		}
		return total.doubleValue();
	}
	
	/**
	 * 添加条目到购物车
	 */
	public void add(CartItem cartItem) {
		//判断车中是否已存在该条目
		if(map.containsKey(cartItem.getBook().getBid())) {
			//先得到已存在的条目
			CartItem oldCartItem=map.get(cartItem.getBook().getBid());
			//修改已存在的条目的数目:在已存在条目的基础上加上新条目的数目
			oldCartItem.setCount(oldCartItem.getCount()+cartItem.getCount());
			//把修改后的条目添加到购物车(map)中
			map.put(cartItem.getBook().getBid(), oldCartItem);
		} else {
			//直接把新条目添加到购物车(map)中
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	
	/**
	 * 清空购物车所有条目
	 */
	public void clear() {
		map.clear();
	}
	
	
	/**
	 * 删除购物车指定条目
	 */
	public void delete(String bid) {
		map.remove(bid);
	}
	
	/**
	 * 获取购物车所有条目
	 */
	public Collection<CartItem> getCartItems() {
		return map.values();
	}
	
}
