package com.polaris.core.util;

import com.polaris.core.pojo.Page;

/**
 * 分页工具
 *
 */
public class PageUtil {

    public static final int DEFAULT_PAGE_SIZE = 10;

    public static int[] init(Page<?> page) {
        page.setPageNo(page.getPageNo()==-1?1:page.getPageNo());
        page.setPageSize(page.getPageSize() == -1?DEFAULT_PAGE_SIZE:page.getPageSize());
        int firstResult = page.getFirst() - 1;
        int maxResults = page.getPageSize();
        return new int[]{firstResult, maxResults};
    }

}
