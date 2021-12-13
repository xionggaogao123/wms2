package com.huanhong.wms.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SelectTree implements Serializable {

    private String label;
    private Object value;
    private List<SelectTree> children;

}
