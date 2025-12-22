package com.qcl.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@Document(indexName = "agent_basic")
public class AgentBasic implements Serializable {
    private static final long serialVersionUID = -1L;

    @Field(name = "analyzer_ip")
    private String analyzerIp;

    @Field(name = "arch")
    private String arch;

    @Field(name = "arch_type")
    private Long archType;

    @Field(name = "az")
    private String az;

    @Field(name = "az_name")
    private String azName;

    @Field(name = "complete_revision")
    private String completeRevision;

    @Field(name = "controller_ip")
    private String controllerIp;

    @Field(name = "cpu_num")
    private Long cpuNum;

    @Field(name = "create_time", type = FieldType.Date)
    private String createTime;

    @Field(name = "ctrl_ip")
    private String ctrlIp;

    @Field(name = "ctrl_mac")
    private String ctrlMac;

    @Field(name = "cur_analyzer_ip")
    private String curAnalyzerIp;

    @Field(name = "cur_controller_ip")
    private String curControllerIp;

    @Field(name = "current_k8s_image")
    private String currentK8sImage;

    @Field(name = "kernel_version")
    private String kernelVersion;

    @Field(name = "launch_server")
    private String launchServer;

    @Id
    @Field(name = "lcuuid")
    private String lcuuid;

    @Field(name = "license_type")
    private String licenseType;

    @Field(name = "memory_size")
    private Float memorySize;

    @Field(name = "name")
    private String name;

    @Field(name = "os")
    private String os;

    @Field(name = "pod_cluster_name")
    private String podClusterName;

    @Field(name = "region_name")
    private String regionName;

    @Field(name = "revision")
    private String revision;

    @Field(name = "state")
    private String state;

    @Field(name = "tap_mode")
    private Long tapMode;

    @Field(name = "update_time", type = FieldType.Date)
    private String updateTime;

    @Field(name = "vtap_group_lcuuid")
    private String vtapGroupLcuuid;

    @Field(name = "vtap_group_name")
    private String vtapGroupName;

    @Field(name = "user_id")
    private String userId;

}
