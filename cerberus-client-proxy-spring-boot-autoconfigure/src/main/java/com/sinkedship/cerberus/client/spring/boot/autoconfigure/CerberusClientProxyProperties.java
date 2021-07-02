package com.sinkedship.cerberus.client.spring.boot.autoconfigure;

import com.sinkedship.cerberus.commons.DataCenter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Derrick Guan
 */
@ConfigurationProperties(prefix = CerberusClientProxyProperties.CERBERUS_CLIENT_PREFIX)
public class CerberusClientProxyProperties {

    public static final String CERBERUS_CLIENT_PREFIX = "cerberus.client.proxy";

    private DataCenter dataCenter;

    private Long connectTimeout;

    private Long requestTimeout;

    private Boolean connectionPoolEnable;

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Boolean getConnectionPoolEnable() {
        return connectionPoolEnable;
    }

    public void setConnectionPoolEnable(Boolean connectionPoolEnable) {
        this.connectionPoolEnable = connectionPoolEnable;
    }
}
