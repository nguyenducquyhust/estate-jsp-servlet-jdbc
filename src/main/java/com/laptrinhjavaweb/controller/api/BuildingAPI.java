package com.laptrinhjavaweb.controller.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptrinhjavaweb.dto.BuildingDTO;
import com.laptrinhjavaweb.paging.PageRequest;
import com.laptrinhjavaweb.paging.Sorter;
import com.laptrinhjavaweb.service.IBuildingService;
import com.laptrinhjavaweb.service.impl.BuildingService;
import com.laptrinhjavaweb.utils.HttpUtils;

@WebServlet(urlPatterns = "/api-admin-building")
public class BuildingAPI extends HttpServlet {

	private static final long serialVersionUID = -915988021506484384L;
	private IBuildingService buildingService;

	public BuildingAPI() {
		buildingService = new BuildingService();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		BuildingDTO buildingDTO = HttpUtils.of(request.getReader()).toModel(BuildingDTO.class);
		buildingDTO = buildingService.save(buildingDTO);
		mapper.writeValue(response.getOutputStream(), buildingDTO);
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		BuildingDTO buildingDTO = HttpUtils.of(request.getReader()).toModel(BuildingDTO.class);
		buildingDTO = buildingService.set(buildingDTO);
		mapper.writeValue(response.getOutputStream(), buildingDTO);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		BuildingDTO buildingDTO = HttpUtils.of(request.getReader()).toModel(BuildingDTO.class);
		try {
			buildingService.del(buildingDTO.getId());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mapper.writeValue(response.getOutputStream(), "{}");

	}

//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		ObjectMapper mapper = new ObjectMapper();
//		req.setCharacterEncoding("UTF-8");
//		resp.setContentType("application/json");
//		Long id = Long.parseLong(req.getParameter("id"));
//		BuildingDTO buildingDTO = HttpUtils.of(req.getReader()).toModel(BuildingDTO.class);
//		buildingDTO = buildingService.searchById(id);
//		mapper.writeValue(resp.getOutputStream(), buildingDTO);
//	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, Object> properties = new HashMap<>();
		properties.put("ward", "asbc");
		properties.put("street", "abc");
		
		Sorter sorter = new Sorter("id", "ASC");
		PageRequest pageRequest= new PageRequest(4, 1, sorter);
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		List<BuildingDTO> buildingDTO = buildingService.findAll(properties,pageRequest);
		mapper.writeValue(resp.getOutputStream(), buildingDTO);
	}

	

}
