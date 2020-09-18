package com.polaris.container.gateway.util;

import java.util.LinkedHashSet;
import java.util.Set;

import com.polaris.core.Constant;
import com.polaris.core.util.StringUtil;

public abstract class FileReaderUtil {

    public static Set<String> getDataSet(String content) {
        Set<String> data = new LinkedHashSet<>();
        if  (StringUtil.isNotEmpty(content)) {
            String[] contents = content.split(Constant.LINE_SEP);
            for (String conf : contents) {
                if (StringUtil.isNotEmpty(conf)) {
                    conf = conf.replace(Constant.NEW_LINE, Constant.EMPTY).trim();
                    conf = conf.replace(Constant.RETURN, Constant.EMPTY).trim();
                    data.add(conf);
                }
            }
        }
        return data;
    }
}
