package cn.philip.core.config;

import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 动态数据源加载
 * @author: pfliu
 * @time: 2020/5/19
 */
public class DynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {

    public DynamicDataSourceProvider(String driverClassName, String url, String username, String password) {
        super(driverClassName, url, username, password);
    }

    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        Map<String, DataSourceProperty> map = new HashMap<>();
        ResultSet rs = statement.executeQuery("select * from config_datasource where state = 1");
        while(rs.next()){
            String driver = rs.getString("driver");
            String url = rs.getString("url");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String sourceName = rs.getString("source_name");
            DataSourceProperty dataSourceProperty = new DataSourceProperty();
            dataSourceProperty.setDriverClassName(driver);
            dataSourceProperty.setUrl(url);
            dataSourceProperty.setUsername(username);
            dataSourceProperty.setPassword(password);
            dataSourceProperty.setContinueOnError(true);
            map.put(sourceName,dataSourceProperty);
        }
        return map;
    }
}
