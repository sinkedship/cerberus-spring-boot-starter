package com.sinkedship.cerberus.server.spring.boot.autoconfigure;

import com.sinkedship.cerberus.commons.DataCenter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Derrick Guan
 */
@ConfigurationProperties(prefix = CerberusServerBootstrapProperties.CERBERUS_SERVER_PREFIX)
public class CerberusServerBootstrapProperties {

    public static final String CERBERUS_SERVER_PREFIX = "cerberus.server.bootstrap";

    // data center used to register service information
    // cerberus.server.bootstrap.data-center
    private DataCenter dataCenter;

    // service host address information used to register in data center
    // cerberus.server.bootstrap.register-host
    private String registerHost;

    // host used to bind for service
    // cerberus.server.bootstrap.bind-host
    private String bindHost;

    // port used to bind for service
    // cerberus.server.bootstrap.bind-port
    private Integer bindPort;

    // cerberus.server.bootstrap.accept-backlog
    private Integer acceptBacklog;

    // cerberus.server.bootstrap.io-thread-count
    private Integer ioThreadCount;

    // cerberus.server.bootstrap.worker-thread-count
    private Integer workerThreadCount;

    // cerberus.server.bootstrap.request-timeout
    // request timeout for thrift request, measure in millisecond
    private Long requestTimeout;

    // cerberus.server.bootstrap.executor-thread-count
    private Integer executorThreadCount;

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public String getRegisterHost() {
        return registerHost;
    }

    public void setRegisterHost(String registerHost) {
        this.registerHost = registerHost;
    }

    public String getBindHost() {
        return bindHost;
    }

    public void setBindHost(String bindHost) {
        this.bindHost = bindHost;
    }

    public Integer getBindPort() {
        return bindPort;
    }

    public void setBindPort(Integer bindPort) {
        this.bindPort = bindPort;
    }

    public Integer getAcceptBacklog() {
        return acceptBacklog;
    }

    public void setAcceptBacklog(Integer acceptBacklog) {
        this.acceptBacklog = acceptBacklog;
    }

    public Integer getIoThreadCount() {
        return ioThreadCount;
    }

    public void setIoThreadCount(Integer ioThreadCount) {
        this.ioThreadCount = ioThreadCount;
    }

    public Integer getWorkerThreadCount() {
        return workerThreadCount;
    }

    public void setWorkerThreadCount(Integer workerThreadCount) {
        this.workerThreadCount = workerThreadCount;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getExecutorThreadCount() {
        return executorThreadCount;
    }

    public void setExecutorThreadCount(Integer executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }
}
