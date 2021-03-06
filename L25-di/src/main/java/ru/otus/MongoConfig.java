package ru.otus;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.repository.MongoDbInitializer;
import ru.otus.repository.UserRepository;

@Configuration
@ComponentScan
@PropertySource("classpath:app.properties")
public class MongoConfig {

  @Value("${mongo.url}")
  private String hostDb;

  @Value("${mongo.dbName}")
  private String dbName;

  @Bean
  public MongoClient getMongoClient(String url) {
    return MongoClients.create(url);
  }

  @Bean
  public MongoOperations mongoOperations() {

    MongoOperations mongoOperations = null;
    try {
      mongoOperations = new MongoTemplate(getMongoClient(hostDb), dbName);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mongoOperations;
  }

  @Bean(initMethod = "init")
  public MongoDbInitializer createMongoDbInitializer(UserRepository userRepository) {
    return new MongoDbInitializer(new MongoTemplate(getMongoClient(hostDb), dbName));
  }


}
