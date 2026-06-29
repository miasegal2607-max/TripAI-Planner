package com.tripai.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:5432}")
    private String dbPort;

    @Value("${DB_NAME:tripai}")
    private String dbName;

    @Value("${DB_USER:sa}")
    private String dbUser;

    @Value("${DB_PASS:}")
    private String dbPass;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30000);

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Render provides postgresql:// or postgres:// — parse it manually
            try {
                String clean = databaseUrl
                    .replace("postgresql://", "")
                    .replace("postgres://", "");
                // clean = user:pass@host[:port]/dbname
                String[] atSplit = clean.split("@");
                String[] credParts = atSplit[0].split(":", 2);
                String user = credParts[0];
                String pass = credParts.length > 1 ? credParts[1] : "";
                String[] hostDb = atSplit[1].split("/", 2);
                String[] hostPort = hostDb[0].split(":");
                String host = hostPort[0];
                int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 5432;
                String db = hostDb.length > 1 ? hostDb[1] : "tripai";
                // Remove any query params from db name
                if (db.contains("?")) db = db.substring(0, db.indexOf("?"));

                config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + db);
                config.setUsername(user);
                config.setPassword(pass);
            } catch (Exception e) {
                // Fallback: just prepend jdbc: if missing
                String jdbcUrl = databaseUrl.startsWith("jdbc:") ? databaseUrl : "jdbc:" + databaseUrl;
                config.setJdbcUrl(jdbcUrl);
            }
        } else {
            // Use individual env vars (also set by render.yaml)
            config.setJdbcUrl("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
        }

        return new HikariDataSource(config);
    }
                  }
