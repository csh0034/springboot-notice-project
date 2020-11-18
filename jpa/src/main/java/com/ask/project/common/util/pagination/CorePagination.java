package com.ask.project.common.util.pagination;

import java.util.List;

public class CorePagination<T> extends CorePaginationParam {
	private static final long serialVersionUID = 1356542032092086089L;
	private Long totalItems;
	private List<T> list;


    public CorePagination(CorePaginationInfo info, List<T> list) {
        this.page = info.getPage();
        this.itemsPerPage = info.getItemsPerPage();
        this.totalItems = info.getTotalItems();
        this.list = list;
    }

	public Long getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
}
