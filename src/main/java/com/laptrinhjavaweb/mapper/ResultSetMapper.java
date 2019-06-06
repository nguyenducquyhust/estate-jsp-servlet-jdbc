package com.laptrinhjavaweb.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.laptrinhjavaweb.annotation.Column;
import com.laptrinhjavaweb.annotation.Entity;

public class ResultSetMapper<T> {
	@SuppressWarnings("unchecked")
	public List<T> mapRow(ResultSet rs, @SuppressWarnings("rawtypes") Class zClass) {
		List<T> results = new ArrayList<>();

		if (zClass.isAnnotationPresent(Entity.class)) {
			try {
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				Field[] fields = zClass.getDeclaredFields();
				while (rs.next()) {
					T object = (T) zClass.newInstance();
					// get giá trị của 1 row trong resultSet và set vào trong Entity
					for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
						String columnName = resultSetMetaData.getColumnName(i + 1);
						Object comlumnValue = rs.getObject(i + 1);
                    //current class
						convertResulsetToEntity(fields,columnName,comlumnValue,object);
						//parent class
						Class<?> parentClass=zClass.getSuperclass();
						while(parentClass!=null) {
							Field[] fieldParents= parentClass.getDeclaredFields();
							//logic convert data
							convertResulsetToEntity(fieldParents,columnName,comlumnValue,object);
							parentClass=parentClass.getSuperclass();
						}
					}
					
					results.add(object);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	private void convertResulsetToEntity(Field[] fields, String columnName, Object comlumnValue, T object) throws IllegalAccessException, InvocationTargetException {
		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				if (column.name().equals(columnName) && comlumnValue != null) {
					BeanUtils.setProperty(object, field.getName(), comlumnValue);
					break;
				}
			}
		}
		
	}
}
