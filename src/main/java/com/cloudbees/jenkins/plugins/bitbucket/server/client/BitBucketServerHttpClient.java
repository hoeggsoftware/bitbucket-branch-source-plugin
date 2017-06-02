package com.cloudbees.jenkins.plugins.bitbucket.server.client;


import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.auth.AuthScope;
import java.util.logging.Logger;
import java.util.logging.Level;
import jenkins.model.Jenkins;
import hudson.ProxyConfiguration;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;

import java.net.Proxy;
import java.net.InetSocketAddress;

public class BitBucketServerHttpClient {

    private static final Logger LOGGER = Logger.getLogger(BitBucketServerHttpClient.class.getName());
    private final String host;
    private final Credentials credentials;
    private final boolean skipVerifySsl;

    public BitBucketServerHttpClient(String host, Credentials credentials, boolean skipVerifySsl) {
        this.host = host;
        this.credentials = credentials;
        this.skipVerifySsl = skipVerifySsl;
    }

    public HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        client.getParams().setConnectionManagerTimeout(10 * 1000);
        client.getParams().setSoTimeout(60 * 1000);
        client.getState().setCredentials(AuthScope.ANY, credentials);
        client.getParams().setAuthenticationPreemptive(true);
        setClientProxyParams(host, client);
        return client;
    }

    private void setClientProxyParams(String host, HttpClient client) {
        Jenkins jenkins = Jenkins.getInstance();
        ProxyConfiguration proxyConfig = null;
        if (jenkins != null) {
            proxyConfig = jenkins.proxy;
        }

        Proxy proxy = Proxy.NO_PROXY;
        if (proxyConfig != null) {
            proxy = proxyConfig.createProxy(host);
        }

        if (proxy.type() != Proxy.Type.DIRECT) {
            final InetSocketAddress proxyAddress = (InetSocketAddress)proxy.address();
            LOGGER.log(Level.FINE, "Jenkins proxy: {0}", proxy.address());
            client.getHostConfiguration().setProxy(proxyAddress.getHostString(), proxyAddress.getPort());
            if (skipVerifySsl) {
                Protocol easyHttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
                client.getHostConfiguration().setHost(host, 443, easyHttps);
            }
            String username = proxyConfig.getUserName();
            String password = proxyConfig.getPassword();
            if (username != null && !"".equals(username.trim())) {
                LOGGER.log(Level.FINE, "Using proxy authentication (user={0})", username);
                client.getState().setProxyCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password));
            }
        }
    }
}
