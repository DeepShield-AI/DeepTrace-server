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
@Document(indexName = "traces")
public class Traces implements Serializable {
    private static final long serialVersionUID = -7954295367806875095L;

    @Field(name = "client_ip")
    @JsonProperty("client_ip")
    private String clientIp;

    @Field(name = "client_port")
    @JsonProperty("client_port")
    private Long clientPort;

    @Field(name = "component_name")
    @JsonProperty("component_name")
    private String componentName;

    @Field(name = "e2e_duration")
    @JsonProperty("e2e_duration")
    private Long e2eDuration;

    @Field(name = "end_time")
    @JsonProperty("end_time")
    private Long endTime;

    @Field(name = "endpoint")
    @JsonProperty("endpoint")
    private String endpoint;

    @Field(name = "protocol")
    @JsonProperty("protocol")
    private String protocol;

    @Field(name = "server_ip")
    @JsonProperty("server_ip")
    private String serverIp;

    @Field(name = "server_port")
    @JsonProperty("server_port")
    private Long serverPort;

    @Field(name = "span_num")
    @JsonProperty("span_num")
    private Long spanNum;

    @Field(name = "spans", type = FieldType.Nested)
    @JsonProperty("spans")
    private List<Span> spans;

    @Field(name = "start_time")
    @JsonProperty("start_time")
    private Long startTime;

    @Field(name = "status_code")
    @JsonProperty("status_code")
    private String statusCode;

    @Id
    @Field(name = "trace_id")
    @JsonProperty("trace_id")
    private String traceId;

    @Data
    public static class Span implements Serializable {
        @Field(name = "content")
        @JsonProperty("content")
        private Content content;

        @Field(name = "context")
        @JsonProperty("context")
        private Context context;

        @Field(name = "metric")
        @JsonProperty("metric")
        private Metric metric;

        @Field(name = "tag")
        @JsonProperty("tag")
        private Tag tag;
    }

    @Data
    public static class Content implements Serializable {
        @Field(name = "req_content")
        @JsonProperty("req_content")
        private String reqContent;

        @Field(name = "resp_content")
        @JsonProperty("resp_content")
        private String respContent;
    }

    @Data
    public static class Context implements Serializable {
        @Field(name = "child_ids")
        @JsonProperty("child_ids")
        private List<String> childIds;

        @Field(name = "parent_id")
        @JsonProperty("parent_id")
        private String parentId;

        @Field(name = "parent_trace_id")
        @JsonProperty("parent_trace_id")
        private String parentTraceId;

        @Field(name = "span_id")
        @JsonProperty("span_id")
        private String spanId;

        @Field(name = "trace_id")
        @JsonProperty("trace_id")
        private String traceId;
    }

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

        @Field(name = "k8s_tag")
        @JsonProperty("k8s_tag")
        private EbpfTag k8sTag;
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
    public static class K8sTag implements Serializable {

    }
}
