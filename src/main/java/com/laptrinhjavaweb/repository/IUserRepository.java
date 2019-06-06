package com.laptrinhjavaweb.repository;

import com.laptrinhjavaweb.entity.UserEntity;

public interface IUserRepository extends GenericJDBC<UserEntity> {
	 Long insert(UserEntity userEntity);
}
