package com.polaris.core.util;

import org.apache.commons.lang3.StringUtils;

import com.polaris.core.dto.Page;
import com.polaris.core.dto.PageDto;

/**
 * 分页工具
 *
 */
public class PageUtil {

    public static final int PAGE_SIZE = 10;

    public static int[] init(Page<?> page, PageDto dto) {
        int pageNumber = dto.getPageIndex();
        page.setPageNo(pageNumber);
        int pageSize = Integer.parseInt(StringUtils.defaultIfBlank(String.valueOf(dto.getPageSize()), String.valueOf(PAGE_SIZE)));
        page.setPageSize(pageSize);
        int firstResult = page.getFirst() - 1;
        int maxResults = page.getPageSize();
        return new int[]{firstResult, maxResults};
    }

}
