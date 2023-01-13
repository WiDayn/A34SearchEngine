package cn.edu.hhu.a34searchengine.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration
{

    @Value("${spring.elasticsearch.host}")
    private String host;


    @Override
    public @NotNull ClientConfiguration clientConfiguration()
    {
        return ClientConfiguration.builder()
                .connectedTo(host)
                .build();
    }
}
