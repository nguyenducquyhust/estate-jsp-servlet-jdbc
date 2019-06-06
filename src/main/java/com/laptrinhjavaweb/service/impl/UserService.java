package com.laptrinhjavaweb.service.impl;

import com.laptrinhjavaweb.dto.UserDTO;
import com.laptrinhjavaweb.entity.UserEntity;
import com.laptrinhjavaweb.service.IUserService;
import com.laptrinhjavawebconverter.UserConverter;

public class UserService implements IUserService {

	@SuppressWarnings("unused")
	@Override
	public UserDTO save(UserDTO newUser) {
		UserConverter userConverter= new UserConverter();
		UserEntity userEntity=userConverter.convertToEntity(newUser);
		return null;
	}

}
