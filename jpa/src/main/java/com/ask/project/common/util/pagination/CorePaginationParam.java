package com.ask.project.common.util.pagination;

import java.io.Serializable;

public class CorePaginationParam implements Serializable {
	private static final long serialVersionUID = 2153149594352856183L;
	protected Long page;
	protected Long itemsPerPage;

	public Long getPage() {
		if (page == null || page < 1) {
			page = 1L;
		}
		return page;
	}
	public void setPage(Long page) {
		if (page == null || page < 1) {
			page = 1L;
		}
		this.page = page;
	}
	public Long getItemsPerPage() {
		if (itemsPerPage == null || itemsPerPage < 1) {
			itemsPerPage = 15L;
		}
		return itemsPerPage;
	}
	public void setItemsPerPage(Long itemsPerPage) {
		if (itemsPerPage == null || itemsPerPage < 1) {
			itemsPerPage = 15L;
		}
		this.itemsPerPage = itemsPerPage;
	}
}
