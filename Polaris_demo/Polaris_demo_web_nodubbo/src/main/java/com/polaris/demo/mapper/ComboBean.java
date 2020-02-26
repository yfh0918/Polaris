package com.polaris.demo.mapper;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;


@Table(name = "combo_tbl")
public class ComboBean {
    @Id
    private Long comboId;
    private String comboName;
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


}
