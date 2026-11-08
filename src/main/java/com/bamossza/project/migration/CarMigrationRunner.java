package com.bamossza.project.migration;

import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.client.MongoDatabase;

/**
 * Batch entry point, enabled with the {@code migrate} profile (together with {@code mongodb}):
 * <pre>java -jar car.jar --spring.profiles.active=mongodb,migrate --migration.direction=oracle-to-mongodb</pre>
 */
@Component
@Profile("migrate")
public class CarMigrationRunner implements CommandLineRunner, ExitCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CarMigrationRunner.class);

    private final MongoDatabase mongoDatabase;
    private final String direction;
    private final String oracleUrl;
    private final String oracleUser;
    private final String oraclePassword;
    private final String reportPath;
    private int exitCode = 1;

    public CarMigrationRunner(MongoDatabase mongoDatabase,
            @Value("${migration.direction:reconcile}") String direction,
            @Value("${migration.oracle.url}") String oracleUrl,
            @Value("${migration.oracle.username}") String oracleUser,
            @Value("${migration.oracle.password}") String oraclePassword,
            @Value("${migration.report:}") String reportPath) {
        this.mongoDatabase = mongoDatabase;
        this.direction = direction;
        this.oracleUrl = oracleUrl;
        this.oracleUser = oracleUser;
        this.oraclePassword = oraclePassword;
        this.reportPath = reportPath;
    }

    @Override
    public void run(String... args) throws Exception {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(oracleUrl, oracleUser, oraclePassword, true);
        try {
            CarMigrationService service = new CarMigrationService(
                    new OracleCarStore(new JdbcTemplate(dataSource)), new MongoCarStore(mongoDatabase));
            CarMigrationService.Direction dir = CarMigrationService.Direction.valueOf(direction.toUpperCase(Locale.ROOT).replace('-', '_'));
            CarMigrationService.MigrationResult result = service.run(dir);
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(result);
            logger.info("migration result:\n{}", json);
            if (!reportPath.isEmpty()) {
                mapper.writeValue(new File(reportPath), result);
            }
            exitCode = result.isSuccessful() ? 0 : 2;
        } finally {
            dataSource.destroy();
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
