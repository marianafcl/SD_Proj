package org.binas.domain;

import java.util.HashMap;

import org.binas.ws.UserView;

public class User {
	//por no BinasManager
	private HashMap<String, Client> useres = new HashMap(); 
	//
	private String email;
	private int saldo = 10;
	private boolean hasBina= false;
	
	public User(String email ) {
		this.email = email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getSaldo() {
		return this.saldo;
	}
	
	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}
	
	public boolean getHasBina() {
		return this.hasBina;
	}
	
	public void setHasBina(boolean hasBina) {
		this.hasBina = hasBina;
	}
}


// colocar na fucking BinasMAnager tmabem 
public UserView activateUser(String email) {
	User user = new User(email);
	useres.put(email, user);
	
	UserView userView = new UserView();
	
	userView.setEmail(email);
	userView.setHasBina(user.getHasBina());
	userView.setCredit(user.getSaldo());
	
	return userView;
	
}