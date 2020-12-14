package com.xxbb.framework.simplespring.mvc.type;

import java.util.Objects;

/**
 * http的请求路径和方法
 * @author xxbb
 */
public class RequestPathInfo {
    /**
     * 请求路径
     */
    private String httpPath;
    /**
     * 请求方法
     */
    private String httpMethod;

    public RequestPathInfo() {
    }

    public RequestPathInfo(String httpPath, String httpMethod) {
        this.httpPath = httpPath;
        this.httpMethod = httpMethod;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "RequestPathInfo{" +
                "httpPath='" + httpPath + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestPathInfo that = (RequestPathInfo) o;
        return Objects.equals(httpPath, that.httpPath) &&
                Objects.equals(httpMethod, that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpPath, httpMethod);
    }
}
