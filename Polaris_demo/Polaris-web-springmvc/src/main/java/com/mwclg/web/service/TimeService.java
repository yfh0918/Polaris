package com.mwclg.web.service;


import java.util.List;

import com.mwclg.web.entity.po.TimeLine;

public interface TimeService {

    List<TimeLine> selectTimeLine(Integer columnId);

}
