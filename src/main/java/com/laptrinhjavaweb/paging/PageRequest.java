package com.laptrinhjavaweb.paging;

public class PageRequest implements Pageable {
	private Integer maxPageItem ;
	private Integer page ;
	private Sorter sorter;
	
	public PageRequest(Integer maxPageItem, Integer page,Sorter sorter) {
		
		this.maxPageItem = maxPageItem;
		this.page = page;
		this.sorter=sorter;
	}


	@Override
	public Integer getPage() {
		return page;
	}

	@Override
	public Integer getOffset() {
		if(page!=null&&maxPageItem!=null) {
			return (page-1)*maxPageItem;
		}
		return null;
	}

	@Override
	public Integer getLimit() {
		return maxPageItem;
	}


	@Override
	public Sorter getSorter() {
		return sorter;
	}






	
}
