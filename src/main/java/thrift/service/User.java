package thrift.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import java.io.Serializable;

/**
 * struct 实体
 */
@ThriftStruct
public final class User implements Serializable {
	private String name;
	private String email;

	public User() {
	}

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@ThriftField(1)
	public String getName() {
		return name;
	}

	@ThriftField
	public void setName(String name) {
		this.name = name;
	}
	@ThriftField(2)
	public String getEmail() {
		return email;
	}

	@ThriftField
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
