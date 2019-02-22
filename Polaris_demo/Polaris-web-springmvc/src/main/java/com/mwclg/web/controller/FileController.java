package com.mwclg.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mwclg.web.entity.po.File;
import com.mwclg.web.service.FileService;
import com.polaris.comm.config.ConfClient;
import com.polaris.core.connect.ServerDiscoveryHandlerProvider;

@RequestMapping("/file")
@Controller
public class FileController extends BaseController {


    @Autowired
    private FileService fileService;

    /**
     * @param period   选传		时间段(all,week,month,season)
     * @param fileName 选传		文件名
     * @param model
     * @return
     * @throws
     * @Title: getFilePageInfo
     * @Description: 根据条件获取文件下载页面的数据
     * @author LiYe
     */
    @RequestMapping(value = "/getFilePageInfo", produces = "text/html;charset=UTF-8")
    public String getFilePageInfo(Model model) {
        try {
            // 根据参数获取的数据
            Map<String, Object> resultMap = fileService.getFilePageInfo(JSONObject.toJSONString(this.getRequestParamters()));
            model.addAttribute("resultMap", resultMap);
            model.addAttribute("page", resultMap.get("pageInfo"));
            model.addAttribute("columnvolist", super.getColumnvolist());
            model.addAttribute("topcolumn", 3);
//            model.addAttribute("commerceUrl", ServerDiscoveryHandlerProvider.getInstance().getUrl(ConfClient.get("URL_COMMERCEURL")));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return getUrlPrefix() + "/fileDownload/fileDownload";
    }

    /**
     * @param model
     * @return
     * @throws
     * @Title: increaseDownloadCount
     * @Description: 下载成功后，下载次数+1
     * @author LiYe
     */
    @RequestMapping(value = "/increaseDownloadCount", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String increaseDownloadCount(Model model) {
        File file = null;
        try {
            // 根据参数获取的数据
            file = fileService.increaseDownloadCount(JSONObject.toJSONString(this.getRequestParamters()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return JSONObject.toJSONString(file);
    }

}
