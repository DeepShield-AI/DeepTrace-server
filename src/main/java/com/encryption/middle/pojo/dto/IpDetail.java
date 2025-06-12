package com.encryption.middle.pojo.dto;

import java.io.Serializable;

public class IpDetail implements Serializable {

    private String ip;

    private String location; // 局域网

    private String tag; // 测试网段一

    private String lat; // 经纬度 0,0

    private String timeZone; //时区

    private String operator; //运营商

    private String systemTag; //系统标签

}
