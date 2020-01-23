package com.polaris.gateway.request;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public enum FilterType {
	
    ARGS("args.txt"),
    COOKIE("cookie.txt"),
    UA("ua.txt"),
    URL("url.txt"),
    WURL("wurl.txt"),
    POST("post.txt"),
    IP("ip.txt"),
    WIP("wip.txt"),
    FILE("file.txt");
    private String fileName;
	
    FilterType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
