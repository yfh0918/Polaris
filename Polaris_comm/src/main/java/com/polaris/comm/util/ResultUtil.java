package com.polaris.comm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.github.pagehelper.Page;
import com.polaris.comm.dto.ResultDto;
import com.polaris.comm.dto.StatusMsg;

import cn.hutool.core.bean.BeanUtil;

public class ResultUtil {

    /**
     * 创建table list类型的数据结果接返回方法
     *
     * @param pageDatas
     * @return
     */
    public static ResultDto createResponseListJson(Page<Map<String, Object>> pageDatas, StatusMsg statusMsg) {
        ResultDto model = new ResultDto();
        model.setStatus(statusMsg.getStatus());
        if (!statusMsg.getStatus().equals(0)) {
            model.setMsgContent(statusMsg.getMsgContent());
            model.setMsgType(ResultDto.MSGTYPE_DANGER);
            return model;
        } else {
            model.setMsgType(ResultDto.MSGTYPE_SUCCESS);
            model.setTotal(new Long(pageDatas.getTotal()).intValue());
            model.setTotalPage(pageDatas.getPages());
            model.setPageSize(pageDatas.getPageSize());
            model.setPageIndex(pageDatas.getPageNum());
            model.setPageFrom(pageDatas.getStartRow());
            model.setPageTo(pageDatas.getEndRow());
            model.setDatas(pageDatas.getResult());
            model.setLeftover(calculateRemainder(model.getTotal(), model.getPageSize(), model.getPageIndex()));
        }
        return model;
    }

    public static <T> ResultDto createResponseList(Page<T> pages, StatusMsg statusMsg) {
        ResultDto model = new ResultDto();
        model.setStatus(statusMsg.getStatus());
        if (!statusMsg.getStatus().equals(0)) {
            model.setMsgContent(statusMsg.getMsgContent());
            model.setMsgType(ResultDto.MSGTYPE_DANGER);
            return model;
        } else {
            model.setMsgType(ResultDto.MSGTYPE_SUCCESS);
            model.setTotal(new Long(pages.getTotal()).intValue());
            model.setTotalPage(pages.getPages());
            model.setPageSize(pages.getPageSize());
            model.setPageIndex(pages.getPageNum());
            model.setPageFrom(pages.getStartRow());
            model.setPageTo(pages.getEndRow());
            model.setDatas(listEntity2ListMap(pages.getResult()));
            model.setLeftover(calculateRemainder(model.getTotal(), model.getPageSize(), model.getPageIndex()));
        }
        return model;
    }

    public static List<Map<String, Object>> convertListMap(List<Map<String, Object>> result) {
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> returnListMap = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> resultMap : result) {
            returnListMap.add(resultMap);
        }
        return returnListMap;
    }

    /**
     * 将对象的list 转换为 map 的list
     *
     * @param list
     */
    public static <T> List<Map<String, Object>> listEntity2ListMap(List<T> list) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (T t : list) {
            resultList.add(BeanUtil.beanToMap(t));
        }
        return resultList;
    }

    /**
     * <p>@describe: 提交 申诉 指派 退回  </P>
     * <p>@param statusmsg
     * <p>@return  </P>
     * <p>@date: 2017年5月16日 下午5:36:43 </P>
     * <p>@author: shangmengsi </P>
     * <p>@remark:    </P>
     */
    public static ResultDto createResponseSaveOrUpdateJson(StatusMsg statusmsg) {
        ResultDto responseModel = new ResultDto();
        responseModel.setStatus(statusmsg.getStatus());
        responseModel.setMsgContent(statusmsg.getMsgContent());
        if (!statusmsg.getStatus().equals(0)) {
            responseModel.setMsgDetail("");
            responseModel.setMsgType(ResultDto.MSGTYPE_DANGER);
            return responseModel;
        } else {
            responseModel.setMsgDetail("");
            responseModel.setMsgType(ResultDto.MSGTYPE_SUCCESS);
        }

        return responseModel;
    }

    /**
     * <p>@describe: 指派人  </P>
     * <p>@param map
     * <p>@param statusmsg
     * <p>@return  </P>
     * <p>@date: 2017年5月16日 下午7:16:16 </P>
     * <p>@author: shangmengsi </P>
     * <p>@remark:    </P>
     */
    public static ResultDto querys(List<Map<String, Object>> map, StatusMsg statusmsg) {
        ResultDto model = new ResultDto();
        model.setStatus(statusmsg.getStatus());
        if (!statusmsg.getStatus().equals(0)) {
            model.setMsgDetail("");
            model.setMsgContent(statusmsg.getMsgContent());
            model.setMsgType(ResultDto.MSGTYPE_DANGER);
            return model;
        } else {
            model.setMsgDetail("");
            model.setMsgType(ResultDto.MSGTYPE_SUCCESS);
            model.setTotal(map.size());
            model.setDatas(map);
        }
        return model;
    }


    public static ResultDto querys(Map<String, Object> map, StatusMsg statusmsg) {
        ResultDto model = new ResultDto();
        model.setStatus(statusmsg.getStatus());
        if (!statusmsg.getStatus().equals(0)) {
            model.setMsgDetail("");
            model.setMsgContent(statusmsg.getMsgContent());
            model.setMsgType(ResultDto.MSGTYPE_DANGER);
            return model;
        } else {
            model.setMsgDetail("");
            model.setMsgType(ResultDto.MSGTYPE_SUCCESS);
            model.setTotal(map.size());
            model.setData(map);
        }
        return model;
    }

    public static ResultDto createResponseDataJson(Page<Map<String, Object>> pageDatas, StatusMsg statusMsg) {
        ResultDto model = new ResultDto();
        model.setStatus(statusMsg.getStatus());
        if (!statusMsg.getStatus().equals(0)) {
            model.setMsgContent(statusMsg.getMsgContent());
            model.setMsgDetail(ResultDto.MSGTYPE_DANGER);
            return model;
        } else {
            model.setMsgDetail("");
            model.setMsgType(ResultDto.MSGTYPE_SUCCESS);
            if (pageDatas != null && !pageDatas.isEmpty()) {
                model.setTotal(new Long(pageDatas.getTotal()).intValue());
                model.setTotalPage(pageDatas.getPages());
                model.setPageSize(pageDatas.getPageSize());
                model.setPageIndex(pageDatas.getPageNum());
                model.setPageFrom(pageDatas.getStartRow());
                model.setPageTo(pageDatas.getEndRow());
                model.setData(pageDatas.get(0));
                model.setLeftover(calculateRemainder(model.getTotal(), model.getPageSize(), model.getPageIndex()));
            }
            return model;
        }
    }

    /**
     * 返回剩下的页数
     *
     * @return
     */
	private static int calculateRemainder(int allCount, int pageSize, int pageIndex)
	{
		int displayCount = pageIndex * pageSize;
		int remainder = allCount - displayCount;
		if(remainder >= pageSize)
		{
			return pageSize;
		}
		else if(remainder > 0)
		{
			return remainder;
		}
		else
		{
			return 0;
		}
	}
}
