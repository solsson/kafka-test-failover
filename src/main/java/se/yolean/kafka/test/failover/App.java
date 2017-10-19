package se.yolean.kafka.test.failover;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.prometheus.client.exporter.HTTPServer;
import se.yolean.kafka.test.failover.config.AppModule;
import se.yolean.kafka.test.failover.config.ConfigModule;
import se.yolean.kafka.test.failover.config.MetricsModule;

public class App {

	static {
		com.github.structlog4j.StructLog4J.setFormatter(com.github.structlog4j.json.JsonFormatter.getInstance());
	}

	public static void main(String[] args) {
		ConfigModule configModule = new ConfigModule();
		Injector injector = Guice.createInjector(configModule, new MetricsModule(), new AppModule());

		String appId = configModule.getConf("KEY_PREFIX", "KT");
		RunId runId = new RunId(appId);

		ProducerConsumerRun run = injector.getInstance(ProducerConsumerRun.class);
		try {
			run.start(runId);
		} finally {
			HTTPServer server = injector.getInstance(HTTPServer.class);
			if (server != null) {
				server.stop();
			}
		}
	}

}
