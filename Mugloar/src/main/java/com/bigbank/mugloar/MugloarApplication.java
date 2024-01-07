package com.bigbank.mugloar;

import com.bigbank.mugloar.config.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import static java.lang.String.format;

@EnableScheduling
@EnableFeignClients
@EnableConfigurationProperties({ApplicationProperties.class})
@SpringBootApplication
@Slf4j
public class MugloarApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MugloarApplication.class);
		Environment env = app.run(args).getEnvironment();
		logApplicationStartup(env);
	}

	private static void logApplicationStartup(Environment env) {
		logServerConfiguration(env);
		logAppConfiguration(env);
	}

	private static void logServerConfiguration(Environment env){
		String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store"))
				.map(key -> "https")
				.orElse("http");
		String serverPort = env.getProperty("server.port");
		String contextPath = Optional
				.ofNullable(env.getProperty("server.servlet.context-path"))
				.filter(StringUtils::isNotBlank)
				.orElse("/");
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}
		log.info(
				"""
                        ----------------------------------------------------------
                        \tApplication '{}' is running! Access URLs:
                        \tLocal: \t\t{}://localhost:{}{}
                        \tExternal: \t{}://{}:{}{}
                        \tProfile(s): \t{}
                        \tDocs available at {}
                        \tActuator available at: \t{}
                        ----------------------------------------------------------""",
				env.getProperty("spring.application.name"),
				protocol,
				serverPort,
				contextPath,
				protocol,
				hostAddress,
				serverPort,
				contextPath,
				env.getActiveProfiles(),
				format("%s://localhost:%s%s%s",protocol,serverPort,contextPath,"swagger-ui/index.html"),
				format("%s://localhost:%s%s%s",protocol,serverPort,contextPath,"actuator")

		);
	}

	private static void logAppConfiguration(Environment env){

		log.info("""
                  \n\tEnvironment Configuration:\n {}\n
                  ----------------------------------------------------------""",
				propertiesToLogAsString(env)
		);
	}

	private static String propertiesToLogAsString(Environment env) {
		StringBuffer propertyLogs = new StringBuffer();
		propertyLogs.append(getProp(env,"SAVE_EXECUTION"));
		propertyLogs.append(getProp(env,"CACHE_INITIAL_CAPACITY"));
		propertyLogs.append(getProp(env,"CACHE_MAXIMUM_SIZE"));
		propertyLogs.append(getProp(env,"CACHE_EXPIRE_AFTER_WRITE_IN_SECONDS"));
		propertyLogs.append(getProp(env,"CACHE_EXPIRE_AFTER_LAST_ACCESS_IN_SECONDS"));
		propertyLogs.append(getProp(env,"ASYNC_ASYNC_EXECUTION"));
		propertyLogs.append(getProp(env,"ASYNC_ASYNC_CALLBACK_URL"));
		propertyLogs.append(getProp(env,"ASYNC_ASYNC_EXECUTOR_POOL_SIZE"));
		propertyLogs.append(getProp(env,"STRATEGY_GOLD_RESERVE_HOT_POT"));
		propertyLogs.append(getProp(env,"STRATEGY_LIVES_ACCEPT_SAFE_LIMIT"));
		propertyLogs.append(getProp(env,"STRATEGY_LIVES_ACCEPT_EASY_LIMIT"));
		propertyLogs.append(getProp(env,"STRATEGY_LIVES_ACCEPT_RISKY_LIMIT"));
		propertyLogs.append(getProp(env,"STRATEGY_LIVES_MIN_SAFE_LEVEL"));
		propertyLogs.append(getProp(env,"STRATEGY_MISSION_NOT_FOUND_THRESHOLD"));
		propertyLogs.append(getProp(env,"THROTTLING_API_THROTTLING_DELAY"));
		propertyLogs.append(getProp(env,"THROTTLING_API_THROTTLING_ON_EXCEPTION_DELAY"));
		return propertyLogs.toString();
	}

	private static String getProp(Environment env, String key) {

	 	Object propertyAsObj =env.getProperty(key);
		 if(propertyAsObj==null){
			 log.warn("Property {} was not found. Will be skipped",key);
			 return "";
		 }
		return format("\n\t %s: %s",key,propertyAsObj.toString());
	}
}
