package org.gradle;
 
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
@SpringBootApplication
@MapperScan(value={"org.gradle.mapper"})
@EnableTransactionManagement
@ServletComponentScan
public class App {
 
    public static void main(String[] args) {
//    	System.setProperty("spring.devtools.restart.enabled", "false");
//    	System.setProperty("spring.profiles.active", "dev");
    	SpringApplication application = new SpringApplication(App.class);
    	application.addListeners(new ApplicationPidFileWriter());
    	application.run(args);
//        SpringApplication.run(App.class, args);
    	
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml");
        sessionFactory.setMapperLocations(res);
        
        return sessionFactory.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
      final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
      return sqlSessionTemplate;
    }
}