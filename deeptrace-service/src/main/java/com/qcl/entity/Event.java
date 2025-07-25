package com.qcl.entity;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventName;

    private EventDetail eventDetail;

    private IpDetail threat;

    private IpDetail suffer;
}
