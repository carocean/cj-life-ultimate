package cj.life.ability.elasticsearch.config;

import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.util.List;

@Component
@EnableConfigurationProperties(ElasticProperties.class)
public class ElasticSearchWorkbin {
    @Autowired
    ElasticProperties properties;

    @ConditionalOnProperty(value = "spring.data.elasticsearch.repositories.enabled", havingValue = "true")
    @SneakyThrows
    protected RestHighLevelClient getClientIgnoringCertVerification() {
        //ignoring ssl certificate verification.
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "elastic123"));

        SSLContextBuilder sslBuilder = SSLContexts.custom()
                .loadTrustMaterial(null, (x509Certificates, s) -> true);
        final SSLContext sslContext = sslBuilder.build();
        List<String> uris = properties.uris;
        HttpHost[] httpHosts = new HttpHost[uris.size()];
        for (int i = 0; i < uris.size(); i++) {
            String url = uris.get(i);
            httpHosts[i] = HttpHost.create(url);
        }
        Header[] compatibilityHeaders = new Header[2];
        compatibilityHeaders[0] = new BasicHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=7");
        compatibilityHeaders[1] = new BasicHeader("Content-Type", "application/vnd.elasticsearch+json;"
                + "compatible-with=7");
        RestHighLevelClient client = new RestHighLevelClient(RestClient
                .builder(httpHosts)
                .setDefaultHeaders(compatibilityHeaders)
//port number is given as 443 since its https schema
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                                .setSSLContext(sslContext)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(
                            RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder.setConnectTimeout(properties.connectionTimeout)
                                .setSocketTimeout(properties.socketTimeout);
                    }
                }));
        return client;
    }
}
