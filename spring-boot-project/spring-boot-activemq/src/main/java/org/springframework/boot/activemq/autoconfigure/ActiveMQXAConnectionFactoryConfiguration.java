/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.activemq.autoconfigure;

import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for ActiveMQ XA {@link ConnectionFactory}.
 *
 * @author Phillip Webb
 * @author Aurélien Leboulanger
 * @author Eddú Meléndez
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(TransactionManager.class)
@ConditionalOnBean(XAConnectionFactoryWrapper.class)
@ConditionalOnMissingBean(ConnectionFactory.class)
class ActiveMQXAConnectionFactoryConfiguration {

	@Primary
	@Bean(name = { "jmsConnectionFactory", "xaJmsConnectionFactory" })
	ConnectionFactory jmsConnectionFactory(ActiveMQProperties properties,
			ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers, XAConnectionFactoryWrapper wrapper,
			ActiveMQConnectionDetails connectionDetails) throws Exception {
		ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory(connectionDetails.getUser(),
				connectionDetails.getPassword(), connectionDetails.getBrokerUrl());
		new ActiveMQConnectionFactoryConfigurer(properties, factoryCustomizers.orderedStream().toList())
			.configure(connectionFactory);
		return wrapper.wrapConnectionFactory(connectionFactory);
	}

	@Bean
	@ConditionalOnBooleanProperty(name = "spring.activemq.pool.enabled", havingValue = false, matchIfMissing = true)
	ActiveMQConnectionFactory nonXaJmsConnectionFactory(ActiveMQProperties properties,
			ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers,
			ActiveMQConnectionDetails connectionDetails) {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionDetails.getUser(),
				connectionDetails.getPassword(), connectionDetails.getBrokerUrl());
		new ActiveMQConnectionFactoryConfigurer(properties, factoryCustomizers.orderedStream().toList())
			.configure(connectionFactory);
		return connectionFactory;
	}

}
