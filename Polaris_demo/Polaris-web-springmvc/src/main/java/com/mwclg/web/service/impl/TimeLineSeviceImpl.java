package com.mwclg.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mwclg.web.entity.po.TimeLine;
import com.mwclg.web.mapper.TimeLineMapper;
import com.mwclg.web.service.TimeService;

@Service("timeLineSevice")
@Transactional
public class TimeLineSeviceImpl implements TimeService {
    @Autowired
    private TimeLineMapper timeLineMapper;

    @Override
    public List<TimeLine> selectTimeLine(Integer columnId) {

        List<TimeLine> list = timeLineMapper.selectTimeLineList(columnId);
        return list;
    }

}
