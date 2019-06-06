package com.laptrinhjavaweb.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.laptrinhjavaweb.dto.BuildingDTO;
import com.laptrinhjavaweb.entity.BuildingEntity;
import com.laptrinhjavaweb.paging.PageRequest;
import com.laptrinhjavaweb.repository.IBuildingRepository;
import com.laptrinhjavaweb.repository.impl.BuildingRepository;
import com.laptrinhjavaweb.service.IBuildingService;
import com.laptrinhjavawebconverter.BuildingConverter;

public class BuildingService implements IBuildingService {

	private IBuildingRepository buildingRepository; 
		
	public BuildingService( ) {
		
		buildingRepository = new BuildingRepository();
	}

	@Override
	public BuildingDTO save(BuildingDTO buildingDTO) {
		BuildingConverter buildingConverter= new BuildingConverter();
		BuildingEntity buildingEntity=buildingConverter.convertToEntity(buildingDTO);
		buildingEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		  buildingRepository.insert(buildingEntity);
		return null;
	}

	@Override
	public BuildingDTO set(BuildingDTO buildingDTO) {
		BuildingConverter buildingConverter= new BuildingConverter();
		BuildingEntity buildingEntity=buildingConverter.convertToEntity(buildingDTO);
		buildingEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
	    buildingRepository.update(buildingEntity);
		return null;
	}

	@Override
	public BuildingDTO del(Long id) throws IllegalArgumentException, IllegalAccessException  {
	    buildingRepository.delete(id);
		return null;
	}

	@Override
	public BuildingDTO searchById(Long id) {
		BuildingConverter buildingConverter= new BuildingConverter();
		BuildingDTO buildingDTO=buildingConverter.convertToDTO(buildingRepository.findById(id));

		return buildingDTO ;
	}

	@Override
	public List<BuildingDTO> findAll(Map<String, Object> properties, PageRequest pageRequest) {
		BuildingConverter buildingConverter= new BuildingConverter();
		List<BuildingDTO> buildingDTOList = new ArrayList<>();
		List<BuildingEntity> buildingEntities= buildingRepository.findAll(properties, pageRequest, null);
		try {
			for(BuildingEntity buildingEntity : buildingEntities ) {
				BuildingDTO buildingDTO= buildingConverter.convertToDTO(buildingEntity);
				buildingDTOList.add(buildingDTO);
				}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return buildingDTOList ;
	
	}
	
	

	




}
