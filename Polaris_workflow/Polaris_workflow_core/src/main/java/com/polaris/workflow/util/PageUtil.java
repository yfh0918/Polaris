package com.polaris.workflow.util;

import org.apache.commons.lang3.StringUtils;

import com.polaris.workflow.api.dto.WorkflowDto;

/**
 * 分页工具
 *
 * @author yufenghua
 */
public class PageUtil {

    public static final int PAGE_SIZE = 10;

    public static int[] init(Page<?> page, WorkflowDto dto) {
        int pageNumber = dto.getPageIndex();
        page.setPageNo(pageNumber);
        int pageSize = Integer.parseInt(StringUtils.defaultIfBlank(String.valueOf(dto.getPageSize()), String.valueOf(PAGE_SIZE)));
        page.setPageSize(pageSize);
        int firstResult = page.getFirst() - 1;
        int maxResults = page.getPageSize();
        return new int[]{firstResult, maxResults};
    }

}
