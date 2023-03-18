package cn.edu.hhu.a34searchengine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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
