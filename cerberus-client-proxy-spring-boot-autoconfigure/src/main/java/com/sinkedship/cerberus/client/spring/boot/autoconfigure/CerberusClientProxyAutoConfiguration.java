package com.sinkedship.cerberus.client.spring.boot.autoconfigure;

import com.sinkedship.cerberus.client.CerberusServiceFactory;
import com.sinkedship.cerberus.client.config.CerberusClientConfig;
import com.sinkedship.cerberus.commons.DataCenter;
import com.sinkedship.cerberus.commons.config.data_center.*;
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
import java.util.concurrent.TimeUnit;

/**
 * @author Derrick Guan
 */
@Configuration
@ConditionalOnClass(CerberusServiceFactory.class)
@ConditionalOnMissingBean(CerberusServiceFactory.class)
@EnableConfigurationProperties({
        CerberusClientProxyProperties.class,
        CerberusClientProxyAutoConfiguration.ZookeeperProperties.class,
        CerberusClientProxyAutoConfiguration.LocalProperties.class,
        CerberusClientProxyAutoConfiguration.ConsulProperties.class,
        CerberusClientProxyAutoConfiguration.EtcdProperties.class,
        CerberusClientProxyAutoConfiguration.K8sProperties.class,
})
public class CerberusClientProxyAutoConfiguration {

    @Bean
    public CerberusServiceFactory cerberusServiceFactory(
            CerberusClientProxyProperties properties,
            ZookeeperProperties zkProperties,
            ConsulProperties consulProperties,
            LocalProperties localProperties,
            EtcdProperties etcdProperties,
            K8sProperties k8sProperties
    ) {
        DataCenter dataCenter = properties.getDataCenter() == null ? DataCenter.LOCAL : properties.getDataCenter();
        CerberusClientConfig config = new CerberusClientConfig(dataCenter);

        setNettyClientConfig(config, properties);
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
            case K8S:
                setK8sConfig(config, k8sProperties);
                break;
            case LOCAL:
                // Local data center does not need any specific configurations by now.
                setLocalConfig(config, localProperties);
                break;
        }
        return new CerberusServiceFactory(config);
    }

    private void setNettyClientConfig(CerberusClientConfig config, CerberusClientProxyProperties properties) {
        if (properties.getConnectTimeout() != null && properties.getConnectTimeout() > 0) {
            config.getDriftNettyClientConfig().setConnectTimeout(
                    new io.airlift.units.Duration(properties.getConnectTimeout(), TimeUnit.MILLISECONDS));
        }
        if (properties.getRequestTimeout() != null && properties.getRequestTimeout() > 0) {
            config.getDriftNettyClientConfig().setRequestTimeout(
                    new io.airlift.units.Duration(properties.getRequestTimeout(), TimeUnit.MILLISECONDS));
        }
    }

    private void setLocalConfig(CerberusClientConfig config, LocalProperties localProperties) {
        LocalConfig localConfig = config.getConcreteDataCenterConfig(LocalConfig.class);
        if (StringUtils.hasText(localProperties.getConnectHost())) {
            localConfig.setConnectHost(localProperties.getConnectHost());
        }
        if (localProperties.getConnectPort() != null && localProperties.getConnectPort() > 0) {
            localConfig.setConnectPort(localProperties.getConnectPort());
        }
    }

    @Bean
    public LocalProperties localProperties() {
        return new LocalProperties();
    }

    @ConfigurationProperties(prefix = "cerberus.data-center.local")
    static class LocalProperties {
        String connectHost;
        Integer connectPort;

        public String getConnectHost() {
            return connectHost;
        }

        public void setConnectHost(String connectHost) {
            this.connectHost = connectHost;
        }

        public Integer getConnectPort() {
            return connectPort;
        }

        public void setConnectPort(Integer connectPort) {
            this.connectPort = connectPort;
        }
    }

    private void setZkConfig(CerberusClientConfig config, ZookeeperProperties zkProperties) {
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

    private void setConsulConfig(CerberusClientConfig config, ConsulProperties properties) {
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

    private void setEtcdConfig(CerberusClientConfig config, EtcdProperties properties) {
        EtcdConfig etcdConfig = config.getConcreteDataCenterConfig(EtcdConfig.class);
        if (StringUtils.hasText(properties.getKeyPrefix())) {
            etcdConfig.setKeyPrefix(properties.getKeyPrefix());
        }
        if (properties.getEndPoints() != null && !properties.getEndPoints().isEmpty()) {
            List<String> endpointList = properties.getEndPoints();
            endpointList.forEach(s -> {
                URI uri = URI.create(s);
                EtcdConfig.Endpoint endpoint = new EtcdConfig.Endpoint(
                        EtcdConfig.Endpoint.Scheme.getSchemeByName(uri.getScheme()),
                        uri.getHost(),
                        uri.getPort());
                etcdConfig.addEndpoint(endpoint);
            });
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
    }

    private void setK8sConfig(CerberusClientConfig config, K8sProperties properties) {
        K8sConfig k8sConfig = config.getConcreteDataCenterConfig(K8sConfig.class);
        if (StringUtils.hasText(properties.getNamespace())) {
            k8sConfig.setNamespace(properties.getNamespace());
        }
        if (StringUtils.hasText(properties.getApiServerHost())) {
            k8sConfig.setApiServerHost(properties.getApiServerHost());
        }
        if (properties.getApiServerPort() != null && properties.getApiServerPort() > 0 &&
                properties.getApiServerPort() < 65536) {
            k8sConfig.setApiServerPort(properties.getApiServerPort());
        }
        if (properties.getVerifySsl() != null) {
            k8sConfig.setVerifySsl(properties.getVerifySsl());
        }
        if (StringUtils.hasText(properties.getAuthToken())) {
            k8sConfig.setAuthToken(properties.getAuthToken());
        }
    }

    @Bean
    public K8sProperties k8sProperties() {
        return new K8sProperties();
    }

    @ConfigurationProperties(prefix = "cerberus.data-center.k8s")
    static class K8sProperties {
        String namespace;
        String apiServerHost;
        Integer apiServerPort;
        Boolean verifySsl;
        String authToken;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getApiServerHost() {
            return apiServerHost;
        }

        public void setApiServerHost(String apiServerHost) {
            this.apiServerHost = apiServerHost;
        }

        public Integer getApiServerPort() {
            return apiServerPort;
        }

        public void setApiServerPort(Integer apiServerPort) {
            this.apiServerPort = apiServerPort;
        }

        public Boolean getVerifySsl() {
            return verifySsl;
        }

        public void setVerifySsl(Boolean verifySsl) {
            this.verifySsl = verifySsl;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }
    }

}
