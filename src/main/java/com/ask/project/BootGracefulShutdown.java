package com.ask.project;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BootGracefulShutdown
		implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

	private static final int TIMEOUT = 30;

	private volatile Connector connector;

	@Override
	public void customize(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {

		log.debug("Call Graceful Shutdown");

		this.connector.pause();

		Executor executor = this.connector.getProtocolHandler().getExecutor();
		if (executor instanceof ThreadPoolExecutor) {
			try {
				ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
				threadPoolExecutor.shutdown();
				if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
					log.warn("Tomcat thread pool did not shutdown gracefully within " + TIMEOUT
							+ " seconds. Proceeding with forceful shutdown");

					threadPoolExecutor.shutdownNow();

					if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
						log.error("Tomcat thread pool did not terminate");
					}
				}
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
