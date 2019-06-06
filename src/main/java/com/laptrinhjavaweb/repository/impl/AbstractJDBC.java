package com.laptrinhjavaweb.repository.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.laptrinhjavaweb.annotation.Column;
import com.laptrinhjavaweb.annotation.Table;
import com.laptrinhjavaweb.mapper.ResultSetMapper;
import com.laptrinhjavaweb.paging.Pageable;
import com.laptrinhjavaweb.paging.Sorter;
import com.laptrinhjavaweb.repository.GenericJDBC;

public class AbstractJDBC<T> implements GenericJDBC<T> {

	private Class<T> zClass;

	// get zclass ra de lam viec luc run time
	@SuppressWarnings("unchecked")
	public AbstractJDBC() {
		Type type = getClass().getGenericSuperclass();
		ParameterizedType parameterizedType = (ParameterizedType) type;
		zClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}

	private Connection getConnection() {
		String databaseURL = "jdbc:mysql://localhost:3306/estate4month2019";
		String user = "root";
		String password = "root";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL, user, password);
			return conn;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<T> query(String sql, Object... parameters) {
		ResultSetMapper<T> resultSetMapper = new ResultSetMapper<>();
		Connection conn = getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = conn.prepareStatement(sql);
			resultSet = statement.executeQuery();
			if (conn != null) {
				// set prameter to statement
				for (int i = 0; i < parameters.length; i++) {
					int index = i + 1;
					statement.setObject(index, parameters[i]);
				}
				return resultSetMapper.mapRow(resultSet, this.zClass);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void update(String sql, Object... parameters) {
		Connection conn = null;
		PreparedStatement statement = null;
		@SuppressWarnings("unused")
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			statement = conn.prepareStatement(sql);
			if (conn != null) {
				// set prameter to statement
				for (int i = 0; i < parameters.length; i++) {
					int index = i + 1;
					statement.setObject(index, parameters[i]);
				}
				statement.executeUpdate();
				conn.commit();
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public Long insert(String sql, Object... parameters) {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Long id = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			statement = conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
			if (conn != null) {
				for (int i = 0; i < parameters.length; i++) {
					int index = i + 1;
					statement.setObject(index, parameters[i]);
				}
				int rowInserted = statement.executeUpdate();
				resultSet = statement.getGeneratedKeys();
				conn.commit();
				if (rowInserted > 0) {
					while (resultSet.next()) {
						id = resultSet.getLong(1);
					}
				}
				return id;
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("static-access")
	@Override
	public Long insert(Object object) {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Long id = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = createSQLInsert();
			statement = conn.prepareStatement(sql, statement.RETURN_GENERATED_KEYS);
			if (conn != null) {
				// get cac fileds con
				Class<?> zClass = object.getClass();
				Field[] fields = zClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					int index = i + 1;
					Field field = fields[i];
					field.setAccessible(true);// xet quyen
					statement.setObject(index, field.get(object));
				}
				// get filed cha
				Class<?> parentClass = zClass.getSuperclass();
				int indexParent = fields.length + 1;

				while (parentClass != null) {
					// logic convert data
					for (int i = 0; i < parentClass.getDeclaredFields().length; i++) {
						Field field = parentClass.getDeclaredFields()[i];
						field.setAccessible(true);// xet quyen
						statement.setObject(indexParent, field.get(object));
						indexParent = indexParent + 1;
					}
					parentClass = parentClass.getSuperclass();
				}

				int rowInserted = statement.executeUpdate();
				resultSet = statement.getGeneratedKeys();
				conn.commit();
				if (rowInserted > 0) {
					while (resultSet.next()) {
						id = resultSet.getLong(1);
					}
				}
				return id;
			}
		} catch (SQLException | IllegalAccessException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return null;
	}

	private String createSQLInsert() {
		String tableName = "";
		if (zClass.isAnnotationPresent(Table.class)) {
			Table table = zClass.getAnnotation(Table.class);
			tableName = table.name();
		}

		StringBuilder fields = new StringBuilder("");
		StringBuilder params = new StringBuilder("");
		for (Field field : zClass.getDeclaredFields()) {
			if (fields.length() > 1) {
				fields.append(",");
				params.append(",");
			}

			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				fields.append(column.name());
				params.append("?");
			}
		}

		// check parent class
		Class<?> parentClass = zClass.getSuperclass();
		while (parentClass != null) {
			// logic convert data
			for (Field field : parentClass.getDeclaredFields()) {
				if (fields.length() > 1) {
					fields.append(",");
					params.append(",");
				}

				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					fields.append(column.name());
					params.append("?");
				}
			}
			parentClass = parentClass.getSuperclass();
		}

		String sql = "INSERT INTO " + tableName + "(" + fields.toString() + ") VALUES (" + params.toString() + ")";
		return sql;
	}

	@Override
	public void update(Object object) {
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = createSQLUpdate();
			statement = conn.prepareStatement(sql);
			if (conn != null) {
				// get cac fileds con
				Class<?> zClass = object.getClass();
				Field[] fields = zClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					int index = i + 1;
					Field field = fields[i];
					field.setAccessible(true);// xet quyen
					statement.setObject(index, field.get(object));
				}
				// get filed cha
				Class<?> parentClass = zClass.getSuperclass();
				int indexParent = fields.length + 1;
				Object id = null;
				while (parentClass != null) {
					// logic convert data
					for (int i = 0; i < parentClass.getDeclaredFields().length; i++) {

						Field field = parentClass.getDeclaredFields()[i];
						field.setAccessible(true);// xet quyen

						String name = field.getName();
						if (!name.equals("id")) {
							statement.setObject(indexParent, field.get(object));
							indexParent = indexParent + 1;
						} else {
							id = field.get(object);
						}
					}
					parentClass = parentClass.getSuperclass();
				}
				statement.setObject(indexParent, id);
				statement.executeUpdate();
				conn.commit();
			}
		} catch (SQLException | IllegalAccessException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private String createSQLUpdate() {
		String tableName = "";
		if (zClass.isAnnotationPresent(Table.class)) {
			Table table = zClass.getAnnotation(Table.class);
			tableName = table.name();
		}
		StringBuilder sets = new StringBuilder("");
		String where = null;
		for (Field field : zClass.getDeclaredFields()) {

			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				String columName = column.name();
				String value = columName + " = ? ";
				if (!columName.equals("id")) {
					if (sets.length() > 1) {
						sets.append(", ");
					}
					sets.append(value);
				}
			}
		}

		// check parent class
		Class<?> parentClass = zClass.getSuperclass();
		while (parentClass != null) {
			// logic convert data
			for (Field field : parentClass.getDeclaredFields()) {

				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					String columName = column.name();
					String value = columName + " = ? ";
					if (!columName.equals("id")) {
						if (sets.length() > 1) {
							sets.append(", ");
						}
						sets.append(value);
					} else {
						where = "WHERE " + value;
					}
				}
			}
			parentClass = parentClass.getSuperclass();
		}

		String sql = "UPDATE " + tableName + " SET " + sets.toString() + where;
		System.out.println(sql);
		return sql;
	}

	@Override
	public void delete(Long id) throws IllegalArgumentException, IllegalAccessException {
		Connection conn = null;
		PreparedStatement statement = null;

		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String tableName = "";
			if (zClass.isAnnotationPresent(Table.class)) {
				Table table = zClass.getAnnotation(Table.class);
				tableName = table.name();
			}

			String sql = "DELETE FROM " + tableName + " WHERE ID = ?";
			statement = conn.prepareStatement(sql);

			if (conn != null) {
				statement.setObject(1, id);
				statement.executeUpdate();
				conn.commit();
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

//	@Override
//	public <T> T findById(Long id) {
//		BuildingDTO buildingDTO = new BuildingDTO();
//		Connection conn = null;
//		PreparedStatement statement = null;
//		ResultSet rs = null;
//		String tableName = "";
//		BuildingConverter bc = new BuildingConverter();
//		ResultSetMapper<BuildingEntity> rsm = new ResultSetMapper<BuildingEntity>();
//		if (zClass.isAnnotationPresent(Table.class)) {
//			Table table = zClass.getAnnotation(Table.class);
//			tableName = table.name();
//		}
//		try {
//			conn = getConnection();
//			conn.setAutoCommit(false);
//			String sql = "SELECT * FROM " + tableName + " WHERE id = " + id;
//			statement = conn.prepareStatement(sql);
//			rs = statement.executeQuery(sql);
//			
//			List<BuildingEntity> list = rsm.mapRow(rs, zClass);
//			BuildingEntity buildingEntity = list.get(0);
//			buildingDTO = bc.convertToDTO(buildingEntity);
//			conn.commit();
//			
//		} catch (SQLException e) {
//			try {
//				conn.rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//		return (T)buildingDTO;
//
//	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T findById(Long id) {
		ResultSetMapper<T> resultSetMapper = new ResultSetMapper<>();
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		String tableName = "";
		if (zClass.isAnnotationPresent(Table.class)) {
			Table table = zClass.getAnnotation(Table.class);
			tableName = table.name();
		}
		String sql = "SELECT * FROM " + tableName + " WHERE id = ? ";
		try {
			conn = getConnection();
			statement = conn.prepareStatement(sql);
			statement.setObject(1, id);
			resultSet = statement.executeQuery();
			if (conn != null) {
				return resultSetMapper.mapRow(resultSet, this.zClass).get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return null;
	}

	@Override
	public List<T> findAll(Map<String, Object> properties, Pageable pageable,Object...where) {
		ResultSetMapper<T> resultSetMapper = new ResultSetMapper<>();
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		StringBuilder sql = createSQLFindAll(properties);
		
		if(where != null && where.length>0 ) {
			sql.append(where[0]);
		}
		if(pageable.getSorter()!=null) {
			Sorter sorter= pageable.getSorter();
			sql.append(" ORDER BY "+ sorter.getSortName()+" "+sorter.getSortBy()+" ");
		}
		if(pageable!=null) {
			if(pageable.getOffset()!=null&&pageable.getLimit()!=null) {
				sql.append("LIMIT "+pageable.getOffset()+","+pageable.getLimit()+" ");			
			}
		}
		try {
			conn = getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql.toString());
			if (conn != null) {
				return resultSetMapper.mapRow(resultSet, this.zClass);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return null;
	}

	private StringBuilder createSQLFindAll(Map<String, Object> properties) {
		String tableName = "";
		if (zClass.isAnnotationPresent(Table.class)) {
			Table table = zClass.getAnnotation(Table.class);
			tableName = table.name();
		}
			StringBuilder result = new StringBuilder(" SELECT * FROM " + tableName + " WHERE 1=1 ");
			if (properties != null && properties.size() > 0) {
				String[] params = new String[properties.size()];
				Object[] values = new Object[properties.size()];
				int i = 0;
				for (Map.Entry<?, ?> item : properties.entrySet()) {
					params[i] = (String) item.getKey();
					values[i] = item.getValue();
					i++;
				}
				for (int i1 = 0; i1 < params.length; i1++) {
					if(values[i1] instanceof String) {
					result.append(" and LOWER(" + params[i1] + ") LIKE '%" + values[i1] + "%'");
					}
					else if(values[i1] instanceof Integer) {
						result.append(" and " + params[i1] + "="+ values[i1]+ "" );
					}
				}
			}
			return result;
		}
	
}
