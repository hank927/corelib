package com.hank.corelib.http.entity;


import com.hank.corelib.parser.Json;

import java.io.Serializable;

/**
 * Created by hank on 2016/8/12.
 */
public class Entity implements Serializable {

    protected int id;
    /**
     * 默认有效期限是1小时： 60 * 60 * 1000
     */
    private static final long EXPIRE_LIMIT = 60 * 60 * 1000;
    private long mCreateTime;

    public Entity() {
        mCreateTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 在{@link #EXPIRE_LIMIT}时间之内有效，过期作废
     *
     * @return true 表示过期
     */
    public boolean isExpire() {

        //当前时间-保存时间如果超过1天，则认为过期
        return System.currentTimeMillis() - mCreateTime > EXPIRE_LIMIT;
    }

    @Override
    public String toString() {
        return Json.get().toJson(this);
    }
}
