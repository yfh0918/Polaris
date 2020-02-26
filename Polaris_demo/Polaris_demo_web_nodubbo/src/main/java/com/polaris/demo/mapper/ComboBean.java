package com.polaris.demo.mapper;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: lei.chen@hcit.ai
 * @Description:
 * @CreateTiem: 2019/11/28 15:37
 **/
@Table(name = "combo_tbl")
public class ComboBean {
    @Id
    private Long comboId;
    //套餐名称
    private String comboName;
    
    //套餐描述
    private String comboDesc;

    //可使用次数
    private Integer availableUploads;


    private BigDecimal comboAmt;
    
    private Integer comboType;
    
    private Date createTime;
    private Date updateTime;


    public Long getComboId() {
        return comboId;
    }

    public void setComboId(Long comboId) {
        this.comboId = comboId;
    }


    public String getComboName() {
		return comboName;
	}

	public void setComboName(String comboName) {
		this.comboName = comboName;
	}

	public String getComboDesc() {
        return comboDesc;
    }

    public void setComboDesc(String comboDesc) {
        this.comboDesc = comboDesc;
    }

    public Integer getAvailableUploads() {
        return availableUploads;
    }

    public void setAvailableUploads(Integer availableUploads) {
        this.availableUploads = availableUploads;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public BigDecimal getComboAmt() {
        return comboAmt;
    }

    public void setComboAmt(BigDecimal comboAmt) {
        this.comboAmt = comboAmt;
    }

	public Integer getComboType() {
		return comboType;
	}

	public void setComboType(Integer comboType) {
		this.comboType = comboType;
	}
}
