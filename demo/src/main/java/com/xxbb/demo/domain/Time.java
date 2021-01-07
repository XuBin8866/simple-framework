package com.xxbb.demo.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author xubin
 * @date 2020/12/25 15:13
 */
public class Time {
    Integer id;
    Timestamp time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Time{" +
                "id=" + id +
                ", time=" + time +
                '}';
    }
}
