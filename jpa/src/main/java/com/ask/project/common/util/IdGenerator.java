package com.ask.project.common.util;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import com.ask.core.util.CoreUtils.string;

public class IdGenerator implements IdentifierGenerator, Configurable {

	public static final String PARAM_KEY = "ID_PREFIX";

	private String prefix;

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return string.getNewId(prefix);
	}

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		this.prefix = params.getProperty(PARAM_KEY);
	}
}
