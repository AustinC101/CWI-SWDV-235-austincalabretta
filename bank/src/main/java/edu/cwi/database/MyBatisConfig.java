package edu.cwi.database;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import edu.cwi.mappers.AccountMapper;
import edu.cwi.mappers.TransactionMapper;
import edu.cwi.mappers.UserMapper;

public class MyBatisConfig {

    private static final HikariDataSource dataSource =
            createDataSource();

    private static final SqlSessionFactory sqlSessionFactory =
            createSqlSessionFactory();

    private static HikariDataSource createDataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(databaseSetting(
                "BANK_DB_URL",
                "jdbc:mysql://localhost:3306/online_bank"
        ));

        config.setUsername(databaseSetting(
                "BANK_DB_USERNAME", "root"
        ));

        config.setPassword(databaseSetting(
                "BANK_DB_PASSWORD", "MySQLsa"
        ));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }

    private static String databaseSetting(
            String environmentVariable,
            String defaultValue) {

        String value = System.getenv(environmentVariable);

        return value == null ? defaultValue : value;
    }

    private static SqlSessionFactory createSqlSessionFactory() {

        Environment environment = new Environment(
                "development",
                new JdbcTransactionFactory(),
                dataSource
        );

        Configuration configuration =
                new Configuration(environment);

        configuration.setMapUnderscoreToCamelCase(true);

        configuration.addMapper(UserMapper.class);
        configuration.addMapper(AccountMapper.class);
        configuration.addMapper(TransactionMapper.class);

        return new SqlSessionFactoryBuilder()
                .build(configuration);
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void close() {
        dataSource.close();
    }
}