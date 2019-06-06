package com.laptrinhjavaweb.repository;

import java.util.List;
import java.util.Map;

import com.laptrinhjavaweb.paging.Pageable;

public interface GenericJDBC<T> {
  List<T> query(String sql,Object... parameters);
  void update(String sql, Object... parameters);
  Long insert(String sql, Object... parameters);
  Long insert(Object object);
  void update(Object object);
  void delete(Long id) throws IllegalArgumentException, IllegalAccessException;
  @SuppressWarnings("hiding")
  <T> T findById(Long id);
  List<T> findAll(Map<String, Object> properties, Pageable pageable,Object...where);
  
}
