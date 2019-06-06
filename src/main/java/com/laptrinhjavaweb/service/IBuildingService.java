package com.laptrinhjavaweb.service;

import java.util.List;
import java.util.Map;

import com.laptrinhjavaweb.dto.BuildingDTO;
import com.laptrinhjavaweb.paging.PageRequest;

public interface IBuildingService  {
   BuildingDTO save(BuildingDTO buildingDTO);
   BuildingDTO set(BuildingDTO buildingDTO);
   BuildingDTO del(Long id) throws IllegalArgumentException, IllegalAccessException ;
   BuildingDTO searchById(Long id);
   List<BuildingDTO> findAll(Map<String, Object> properties, PageRequest pageRequest);

}
