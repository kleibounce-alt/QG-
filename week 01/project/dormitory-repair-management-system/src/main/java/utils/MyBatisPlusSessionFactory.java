package utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

public class MyBatisPlusSessionFactory {

    private static SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            synchronized (MyBatisPlusSessionFactory.class) {
                if (sqlSessionFactory == null) {
                    try {
                        sqlSessionFactory = createSqlSessionFactory();
                    } catch (Exception e) {
                        throw new RuntimeException("创建 SqlSessionFactory 失败", e);
                    }
                }
            }
        }
        return sqlSessionFactory;
    }

    private static SqlSessionFactory createSqlSessionFactory() throws Exception {
        // 1. 创建数据源（与 mybatis-config.xml 中的配置保持一致）
        DataSource dataSource = new PooledDataSource(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/QG?useSSL=false&serverTimezone=Asia/Shanghai",
                "root",
                "112302kxt"
        );

        // 2. 使用 MyBatis‑Plus 的 SqlSessionFactoryBean
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 3. 加载 MyBatis 配置文件（mybatis-config.xml）
        factoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));

        return factoryBean.getObject();
    }
}