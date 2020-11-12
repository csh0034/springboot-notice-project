package com.ask.core.config;

import java.util.Collections;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;
import net.sf.log4jdbc.tools.LoggingType;

@Configuration
@MapperScan("com.ask.project.**.service.impl")
@EnableTransactionManagement
public class DataSourceConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean
	public DataSource dataSource() {
		Log4jdbcProxyDataSource lpds = new Log4jdbcProxyDataSource(new HikariDataSource(hikariConfig()));

		Log4JdbcCustomFormatter lcf = new Log4JdbcCustomFormatter();
		lcf.setLoggingType(LoggingType.MULTI_LINE);
		lcf.setSqlPrefix("SQL:::\n\t\t");

		lpds.setLogFormatter(lcf);

		return lpds;
	}

	@Bean
	public TransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public TransactionInterceptor transactionAdvice() {
		NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();
		RuleBasedTransactionAttribute txAttribute = new RuleBasedTransactionAttribute();

		txAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		txAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		HashMap<String, TransactionAttribute> txMethods = new HashMap<String, TransactionAttribute>();
		txMethods.put("*", txAttribute);
		txAttributeSource.setNameMap(txMethods);

		return new TransactionInterceptor(txManager(), txAttributeSource);
	}

	@Bean
	public Advisor transactionAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* com.ask.project.**.service.impl.*Impl.*(..))");
		return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:/mybatis/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mappers/*.xml"));

		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
