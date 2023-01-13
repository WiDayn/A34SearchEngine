package cn.edu.hhu.a34searchengine;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class A34SearchEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(A34SearchEngineApplication.class, args);
    }
    /*

    @Bean("restClient")
    public ElasticsearchClient elasticsearchClient() {

        RestClient httpClient = RestClient.builder(new HttpHost("127.0.0.1", 9200)).build();

        ElasticsearchTransport transport = new RestClientTransport(httpClient, new JacksonJsonpMapper());

        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        return esClient;
    }*/

}
