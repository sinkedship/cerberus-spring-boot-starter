package com.sinkedship.cerberus.server.spring.boot.autoconfigure;

import com.sinkedship.cerberus.bootstrap.CerberusServerBootstrap;
import com.sinkedship.cerberus.bootstrap.config.CerberusServerBootConfig;
import com.sinkedship.cerberus.bootstrap.config.CerberusServerConfig;
import com.sinkedship.cerberus.commons.DataCenter;
import com.sinkedship.cerberus.commons.config.data_center.ConsulConfig;
import com.sinkedship.cerberus.commons.config.data_center.EtcdConfig;
import com.sinkedship.cerberus.commons.config.data_center.ZookeeperConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.time.Duration;
import java.util.List;

/**
 * @author Derrick Guan
 */
@Configuration
@ConditionalOnClass({CerberusServerBootstrap.Builder.class})
@ConditionalOnMissingBean(CerberusServerBootstrap.Builder.class)
@EnableConfigurationProperties({
        CerberusServerBootstrapProperties.class,
        CerberusServerBootstrapAutoConfiguration.ZookeeperProperties.class,
        CerberusServerBootstrapAutoConfiguration.ConsulProperties.class,
        CerberusServerBootstrapAutoConfiguration.EtcdProperties.class
})
public class CerberusServerBootstrapAutoConfiguration {

    @Bean
    public CerberusServerBootstrap.Builder bootstrapBuilder(
            CerberusServerBootstrapProperties properties,
            ZookeeperProperties zkProperties,
            ConsulProperties consulProperties,
            EtcdProperties etcdProperties
    ) {
        DataCenter dataCenter = properties.getDataCenter() == null ? DataCenter.LOCAL : properties.getDataCenter();
        CerberusServerBootstrap.Builder builder = new CerberusServerBootstrap.Builder(dataCenter);
        CerberusServerConfig config = new CerberusServerConfig(dataCenter);

        // set boot config
        setBootConfig(config, properties);

        // set data center config
        switch (dataCenter) {
            case ZOOKEEPER:
                setZkConfig(config, zkProperties);
                break;
            case CONSUL:
                setConsulConfig(config, consulProperties);
                break;
            case ETCD:
                setEtcdConfig(config, etcdProperties);
                break;
            case LOCAL:
                // Local data center does not need any specific configurations by now.
                break;
        }
        return builder.withServerConfig(config);
    }

    private void setBootConfig(CerberusServerConfig config, CerberusServerBootstrapProperties properties) {
        CerberusServerBootConfig bootConfig = config.getBootConfig();
        if (StringUtils.hasText(properties.getRegisterHost())) {
            config.getDataCenterConfig().setRegisterHost(properties.getRegisterHost());
        }
        if (StringUtils.hasText(properties.getBindHost())) {
            bootConfig.setHost(properties.getBindHost());
        }
        if (properties.getBindPort() != null) {
            bootConfig.setPort(properties.getBindPort());
        }
        if (properties.getAcceptBacklog() != null && properties.getAcceptBacklog() > 0) {
            bootConfig.setAcceptBacklog(properties.getAcceptBacklog());
        }
        if (properties.getIoThreadCount() != null && properties.getIoThreadCount() > 0) {
            bootConfig.setIoThreadCount(properties.getIoThreadCount());
        }
        if (properties.getWorkerThreadCount() != null && properties.getWorkerThreadCount() > 0) {
            bootConfig.setWorkerThreadCount(properties.getWorkerThreadCount());
        }
        if (properties.getRequestTimeout() != null && properties.getRequestTimeout() > 0) {
            bootConfig.setRequestTimeout(Duration.ofMillis(properties.getRequestTimeout()));
        }
    }

    private void setZkConfig(CerberusServerConfig config, ZookeeperProperties zkProperties) {
        ZookeeperConfig zkConfig = config.getConcreteDataCenterConfig(ZookeeperConfig.class);
        if (StringUtils.hasText(zkProperties.getConnectString())) {
            zkConfig.setZkConnectString(zkProperties.getConnectString());
        }
        if (StringUtils.hasText(zkProperties.getBasePath())) {
            zkConfig.setBasePath(zkProperties.getBasePath());
        }
        if (zkProperties.getSessionTimeout() != null) {
            zkConfig.setZkSessionTimeout(Duration.ofMillis(zkProperties.getSessionTimeout()));
        }
    }

    @Bean
    public ZookeeperProperties zkProperties() {
        return new ZookeeperProperties();
    }

    @ConfigurationProperties(prefix = "cerberus.data-center.zookeeper")
    static class ZookeeperProperties {
        // connection string: $host_1:port_1,$host_2:port_2,...,$host_x:port_x
        String connectString;
        // base path
        String basePath;
        // zookeeper session time-out
        Long sessionTimeout;

        public String getConnectString() {
            return connectString;
        }

        public void setConnectString(String connectString) {
            this.connectString = connectString;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        public Long getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(Long sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }

    private void setConsulConfig(CerberusServerConfig config, ConsulProperties properties) {
        ConsulConfig consulConfig = config.getConcreteDataCenterConfig(ConsulConfig.class);
        if (StringUtils.hasText(properties.getHost())) {
            consulConfig.setHost(properties.getHost());
        }
        if (properties.getPort() != null && properties.getPort() > 0) {
            consulConfig.setPort(properties.getPort());
        }
    }

    @Bean
    public ConsulProperties consulProperties() {
        return new ConsulProperties();
    }

    @ConfigurationProperties(prefix = "cerberus.data-center.consul")
    static class ConsulProperties {
        String host;
        Integer port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    private void setEtcdConfig(CerberusServerConfig config, EtcdProperties properties) {
        EtcdConfig etcdConfig = config.getConcreteDataCenterConfig(EtcdConfig.class);
        if (properties.getEndPoints() != null && !properties.getEndPoints().isEmpty()) {
            List<String> endpointList = properties.getEndPoints();
            for (String s : endpointList) {
                URI uri = URI.create(s);

                EtcdConfig.Endpoint endpoint = new EtcdConfig.Endpoint(
                        EtcdConfig.Endpoint.Scheme.getSchemeByName(uri.getScheme()),
                        uri.getHost(),
                        uri.getPort());

                etcdConfig.addEndpoint(endpoint);
            }
        }
        if (StringUtils.hasText(properties.getKeyPrefix())) {
            etcdConfig.setKeyPrefix(properties.getKeyPrefix());
        }
        if (properties.getServiceTtl() != null) {
            etcdConfig.setServiceTTL(properties.getServiceTtl());
        }
        if (properties.getServiceKeepInterval() != null) {
            etcdConfig.setServiceKeepInterval(properties.getServiceKeepInterval());
        }
    }

    @Bean
    public EtcdProperties etcdProperties() {
        return new EtcdProperties();
    }

    @ConfigurationProperties(prefix = "cerberus.data-center.etcd")
    static class EtcdProperties {
        List<String> endPoints;
        String keyPrefix;
        Long serviceTtl;
        Long serviceKeepInterval;

        public List<String> getEndPoints() {
            return endPoints;
        }

        public void setEndPoints(List<String> endPoints) {
            this.endPoints = endPoints;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public Long getServiceTtl() {
            return serviceTtl;
        }

        public void setServiceTtl(Long serviceTtl) {
            this.serviceTtl = serviceTtl;
        }

        public Long getServiceKeepInterval() {
            return serviceKeepInterval;
        }

        public void setServiceKeepInterval(Long serviceKeepInterval) {
            this.serviceKeepInterval = serviceKeepInterval;
        }
    }
}
