package org.binas.domain;

import java.util.HashMap;

import org.binas.ws.UserView;

public class User {
	
	private String email;
	private int saldo;
	private boolean hasBina= false;
	
	public User(String email) {
		this.email = email;
		this.saldo = BinasManager.getInstance().getUserInitialPoints();
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


