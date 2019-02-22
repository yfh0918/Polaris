package com.mwclg.web.mapper;

import java.util.List;

import com.mwclg.common.db.IBaseMapper;
import com.mwclg.web.entity.po.TimeLine;

public interface TimeLineMapper extends IBaseMapper<TimeLine> {

    List<TimeLine> selectTimeLineList(Integer columnId);

}