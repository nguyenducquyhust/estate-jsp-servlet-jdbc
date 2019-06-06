package com.laptrinhjavaweb.enums;

public enum BuildingType {
	    TANG_TRET("Tầng trệt"),
	    NGUYEN_CAN("Nguyên căn "),
	    NOI_THAT("Nội thất");
	    

	    private String value;

	    BuildingType(String value) {
	        this.value = value;
	    }

	    

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	    
	    
}
