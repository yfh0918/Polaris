package com.polaris.container.gateway.pojo;

public class HttpHtml {
    private String uri;
    private String host;
    private String fileType;
    private String contentType;
    private String filePath;
    private String startup;
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getStartup() {
        return startup;
    }
    public void setStartup(String startup) {
        this.startup = startup;
    }
}
