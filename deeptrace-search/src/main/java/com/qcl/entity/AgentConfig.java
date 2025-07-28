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
@Document(indexName = "agent_config")
public class AgentConfig implements Serializable {
    private static final long serialVersionUID = -1L;

    @Id
    @Field(name = "agent_lcuuid")
    private String agentLcuuid;

    @Field(name = "agent_name")
    private String agentName;

    @Field(name = "analyzer_ip")
    private String analyzerIp;

    @Field(name = "analyzer_port")
    private Long analyzerPort;

    @Field(name = "app_flow_log_spanid")
    private String appFlowLogSpanid;

    @Field(name = "app_flow_log_traceid")
    private String appFlowLogTraceid;

    @Field(name = "app_log_collect_rate")
    private Long appLogCollectRate;

    @Field(name = "app_log_enable_net_pos")
    private String appLogEnableNetPos;

    @Field(name = "app_log_parse_length")
    private Long appLogParseLength;

    @Field(name = "app_perf_metric_data")
    private Boolean appPerfMetricData;

    @Field(name = "bare_udp_vlan")
    private Long bareUdpVlan;

    @Field(name = "call_log_ignore_point")
    private String callLogIgnorePoint;

    @Field(name = "clock_sync")
    private Boolean clockSync;

    @Field(name = "cloud_resource_down")
    private String cloudResourceDown;

    @Field(name = "collect_nic_regex")
    private String collectNicRegex;

    @Field(name = "config_pull_interval")
    private Long configPullInterval;

    @Field(name = "container_cluster_inner_ip_down")
    private String containerClusterInnerIpDown;

    @Field(name = "controller_ip")
    private String controllerIp;

    @Field(name = "controller_port")
    private Long controllerPort;

    @Field(name = "cpu_core_limit")
    private Long cpuCoreLimit;

    @Field(name = "cpu_millicore_limit")
    private Long cpuMillicoreLimit;

    @Field(name = "data_integration_port")
    private Long dataIntegrationPort;

    @Field(name = "data_integration_service")
    private Boolean dataIntegrationService;

    @Field(name = "data_socket")
    private String dataSocket;

    @Field(name = "dispatch_fuse_monitor_interval")
    private Long dispatchFuseMonitorInterval;

    @Field(name = "dispatch_fuse_threshold_mbps")
    private Long dispatchFuseThresholdMbps;

    @Field(name = "dispatch_limit_mbps")
    private Long dispatchLimitMbps;

    @Field(name = "dispatch_socket")
    private String dispatchSocket;

    @Field(name = "flow_log_collect_rate")
    private Long flowLogCollectRate;

    @Field(name = "flow_log_enable_net_pos")
    private String flowLogEnableNetPos;

    @Field(name = "flow_log_ignore_point")
    private String flowLogIgnorePoint;

    @Field(name = "global_dedup")
    private Boolean globalDedup;

    @Field(name = "http_log_proxy_client")
    private String httpLogProxyClient;

    @Field(name = "http_log_xrequestid")
    private String httpLogXrequestid;

    @Field(name = "inactive_ip_metric_data")
    private Boolean inactiveIpMetricData;

    @Field(name = "inactive_port_metric_data")
    private Boolean inactivePortMetricData;

    @Field(name = "inner_additional_header")
    private String innerAdditionalHeader;

    @Field(name = "log_file_size_mb")
    private Long logFileSizeMb;

    @Field(name = "log_level")
    private String logLevel;

    @Field(name = "log_send")
    private Boolean logSend;

    @Field(name = "log_send_rate_per_hour")
    private Long logSendRatePerHour;

    @Field(name = "log_store_days")
    private Long logStoreDays;

    @Field(name = "max_escape_time")
    private Long maxEscapeTime;

    @Field(name = "memory_limit_mb")
    private Long memoryLimitMb;

    @Field(name = "metric_data")
    private Boolean metricData;

    @Field(name = "net_perf_metric_data")
    private Boolean netPerfMetricData;

    @Field(name = "packet_length")
    private Long packetLength;

    @Field(name = "packet_limit_kpps")
    private Long packetLimitKpps;

    @Field(name = "pcap_socket")
    private String pcapSocket;

    @Field(name = "process_limit")
    private Long processLimit;

    @Field(name = "request_nat_ip")
    private Boolean requestNatIp;

    @Field(name = "resource_report_interval")
    private Long resourceReportInterval;

    @Field(name = "second_metric_data")
    private Boolean secondMetricData;

    @Field(name = "so_plugins")
    private String soPlugins;

    @Field(name = "sync_resource_info")
    private Boolean syncResourceInfo;

    @Field(name = "system_idle_mem_percent")
    private Long systemIdleMemPercent;

    @Field(name = "system_load_fuse_metric")
    private String systemLoadFuseMetric;

    @Field(name = "system_load_fuse_recover")
    private Float systemLoadFuseRecover;

    @Field(name = "system_load_fuse_threshold")
    private Float systemLoadFuseThreshold;

    @Field(name = "thread_limit")
    private Long threadLimit;

    @Field(name = "traffic_api")
    private String trafficApi;

    @Field(name = "traffic_filter")
    private String trafficFilter;

    @Field(name = "traffic_mode")
    private Long trafficMode;

    @Field(name = "tunnel_decap_type")
    private String tunnelDecapType;

    @Field(name = "udp_max_mtu")
    private Long udpMaxMtu;

    @Field(name = "vm_mac_parse")
    private String vmMacParse;

    @Field(name = "vm_xml_folder")
    private String vmXmlFolder;

    @Field(name = "wasm_plugins")
    private String wasmPlugins;
}
