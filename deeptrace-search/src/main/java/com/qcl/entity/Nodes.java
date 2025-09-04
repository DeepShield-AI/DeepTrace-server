package com.qcl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode
@Document(indexName = "nodes")
public class Nodes implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field(name = "node_id")
    @JsonProperty("node_id")
    private Long nodeId;

    @Field(name = "metric")
    @JsonProperty("metric")
    private Metric metric;

    @Field(name = "tag")
    @JsonProperty("tag")
    private Tag tag;

    @Field(name = "trace_tags")
    @JsonProperty("trace_tags")
    private TraceTags traceTags;

    @Field(name = "context")
    @JsonProperty("context")
    private Context context;

    @Field(name = "status_code")
    @JsonProperty("status_code")
    private String statusCode;

    @Field(name = "component")
    @JsonProperty("component")
    private String component;

    // Nested classes similar to Edges
    @Data
    public static class Metric implements Serializable {
        @Field(name = "duration")
        @JsonProperty("duration")
        private Long duration;

        @Field(name = "end_time")
        @JsonProperty("end_time")
        private Long endTime;

        @Field(name = "req_size")
        @JsonProperty("req_size")
        private Long reqSize;

        @Field(name = "resp_size")
        @JsonProperty("resp_size")
        private Long respSize;

        @Field(name = "start_time")
        @JsonProperty("start_time")
        private Long startTime;
    }

    @Data
    public static class Tag implements Serializable {
        @Field(name = "docker_tag")
        @JsonProperty("docker_tag")
        private DockerTag dockerTag;

        @Field(name = "ebpf_tag")
        @JsonProperty("ebpf_tag")
        private EbpfTag ebpfTag;
    }

    @Data
    public static class DockerTag implements Serializable {
        @Field(name = "container_id")
        @JsonProperty("container_id")
        private String containerId;

        @Field(name = "container_name")
        @JsonProperty("container_name")
        private String containerName;

        @Field(name = "created", type = FieldType.Date)
        @JsonProperty("created")
        private Date created;

        @Field(name = "gateway")
        @JsonProperty("gateway")
        private String gateway;

        @Field(name = "hostname")
        @JsonProperty("hostname")
        private String hostname;

        @Field(name = "image")
        @JsonProperty("image")
        private String image;

        @Field(name = "ip")
        @JsonProperty("ip")
        private String ip;

        @Field(name = "network_mode")
        @JsonProperty("network_mode")
        private String networkMode;

        @Field(name = "tgid")
        @JsonProperty("tgid")
        private Long tgid;
    }

    @Data
    public static class EbpfTag implements Serializable {
        @Field(name = "component")
        @JsonProperty("component")
        private String component;

        @Field(name = "direction")
        @JsonProperty("direction")
        private String direction;

        @Field(name = "dst_ip")
        @JsonProperty("dst_ip")
        private String dstIp;

        @Field(name = "dst_port")
        @JsonProperty("dst_port")
        private Long dstPort;

        @Field(name = "endpoint")
        @JsonProperty("endpoint")
        private String endpoint;

        @Field(name = "pid")
        @JsonProperty("pid")
        private Long pid;

        @Field(name = "protocol")
        @JsonProperty("protocol")
        private String protocol;

        @Field(name = "req_seq")
        @JsonProperty("req_seq")
        private Long reqSeq;

        @Field(name = "resp_seq")
        @JsonProperty("resp_seq")
        private Long respSeq;

        @Field(name = "src_ip")
        @JsonProperty("src_ip")
        private String srcIp;

        @Field(name = "src_port")
        @JsonProperty("src_port")
        private Long srcPort;

        @Field(name = "tgid")
        @JsonProperty("tgid")
        private Long tgid;
    }

    @Data
    public static class TraceTags implements Serializable {
        @Field(name = "component_names")
        @JsonProperty("component_names")
        private List<String> componentNames;

        @Field(name = "endpoints")
        @JsonProperty("endpoints")
        private List<String> endpoints;

        @Field(name = "ips")
        @JsonProperty("ips")
        private List<String> ips;

        @Field(name = "protocols")
        @JsonProperty("protocols")
        private List<String> protocols;

        @Field(name = "status_codes")
        @JsonProperty("status_codes")
        private List<String> statusCodes;
    }

    @Data
    public static class Context implements Serializable {
        @Field(name = "trace_id")
        @JsonProperty("trace_id")
        private String traceId;

        @Field(name = "span_id")
        @JsonProperty("span_id")
        private String spanId;

        @Field(name = "parent_id")
        @JsonProperty("parent_id")
        private String parentId;

        @Field(name = "parent_trace_id")
        @JsonProperty("parent_trace_id")
        private String parentTraceId;

        @Field(name = "child_ids")
        @JsonProperty("child_ids")
        private List<String> childIds;
    }
}
