package com.polaris.conf.admin.controller;

import java.util.List;
import java.util.Map;

import com.polaris.comm.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.conf.admin.controller.annotation.PermessionLimit;
import com.polaris.conf.admin.core.model.ConfGroup;
import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.model.ConfZK;
import com.polaris.conf.admin.core.util.ReturnT;
import com.polaris.conf.admin.dao.ConfGroupDao;
import com.polaris.conf.admin.dao.ConfZKDao;
import com.polaris.conf.admin.service.ConfNodeService;

/**
 * 配置管理
 */
@Controller
@RequestMapping("/conf")
public class ConfController {

	@Autowired
	private ConfGroupDao confGroupDao;
	@Autowired
	private ConfZKDao confZKDao;
	@Autowired
	private ConfNodeService confNodeService;
	
	@RequestMapping("")
	@PermessionLimit
	public String index(Model model, String znodeKey){

		List<ConfZK> zklist = confZKDao.findAll();
		List<ConfGroup> list = confGroupDao.findAll();
		model.addAttribute("ConfNodeZK", zklist);
		model.addAttribute("ConfNodeGroup", list);
		return "conf/conf.index";
	}

	@RequestMapping("/findList")
	@ResponseBody
	@PermessionLimit
	public Map<String, Object> findList(String nodeZK, String nodeGroup, String nodeKey) {
		synchronized(this) {
			return confNodeService.findList(nodeZK, nodeGroup, nodeKey);
		}
	}
	
	/**
	 * get
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> delete(String nodeZK, String nodeGroup, String nodeKey){
		synchronized(this) {
			return confNodeService.logicDeleteByKey(nodeZK, nodeGroup, nodeKey);
		}
	}
	
	/**
	 * get
	 * @return
	 */
	@RequestMapping("/recovery")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> recovery(String nodeZK, String nodeGroup, String nodeKey){
		synchronized(this) {
			return confNodeService.recovery(nodeZK, nodeGroup, nodeKey);
		}
	}

	/**
	 * copy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/copy")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> copy(ConfNode confNode){
		synchronized(this) {
			Map<String,Object> result0 = confNodeService.findList(confNode.getNodeZK(), confNode.getNodeGroup(), null);
			if (result0 != null) {
				List<ConfNode> datas = (List<ConfNode>)result0.get("data");
				if (datas != null && datas.size() > 0) {
					return new ReturnT<String>(500, confNode.getNodeGroup()+"已经存在配置项，不可复制");
				}
			}
			
			Map<String,Object> result = confNodeService.findList(confNode.getNodeZK(), confNode.getNodeGroupCopy(), null);
			if (result != null) {
				List<ConfNode> datas = (List<ConfNode>)result.get("data");
				if (datas != null && datas.size() > 0) {
					for (ConfNode node : datas) {
						node.setNodeGroup(confNode.getNodeGroup());
						confNodeService.add(node);
					}
					return new ReturnT<String>(200, "复制完成");
				}
			}
			return new ReturnT<String>(500, "复制源"+confNode.getNodeGroupCopy()+"不存在配置项");
		}
	}
	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> add(ConfNode confNode){
		synchronized(this) {
			return confNodeService.add(confNode);
		}
	}
	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> update(ConfNode confNode){
		synchronized(this) {
			return confNodeService.update(confNode);
		}
	}

	/**
	 * 同步zk
	 * @return
	 */
	@RequestMapping("/synzk")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> synzk(String nodeZK, String nodeGroup, String nodeKey){
		synchronized(this) {
			return confNodeService.synzk(nodeZK, nodeGroup, nodeKey);
		}
	}


    /**
     * 自动同步所有zk
     * @return
     */
    @RequestMapping(value = "/synzkAll", method= RequestMethod.GET)
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> synzkAll(){
        synchronized(this) {
            return confNodeService.synzkAll();
        }
    }

    /**
     * 对value值进行加密
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/des", method= RequestMethod.GET)
    @PermessionLimit(limit = false)
    @ResponseBody
    public  ReturnT<String> des(@RequestParam("value") String value){
        synchronized(this) {
            ReturnT returnT = null;
            try {
                returnT = new ReturnT(EncryptUtil.getInstance().encrypt(EncryptUtil.START_WITH,value));
                returnT.setMsg("加密成功");
            } catch (Exception e) {
                returnT  = new ReturnT(500, e.getMessage());
                returnT.setMsg("加密失败");
            }
            return returnT;
        }
    }

}
