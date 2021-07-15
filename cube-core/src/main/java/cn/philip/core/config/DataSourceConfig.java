package cn.philip.core.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: pfliu
 * @time: 2020/5/19
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider(){
        return new DynamicDataSourceProvider(driverClassName, url, username, password);
    }

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
