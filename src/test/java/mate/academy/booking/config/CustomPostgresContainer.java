package mate.academy.booking.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgresContainer extends PostgreSQLContainer<CustomPostgresContainer> {
    private static final String POSTGRES_IMAGE = "postgres:15.3";

    private static CustomPostgresContainer container;

    private CustomPostgresContainer() {
        super(POSTGRES_IMAGE);
    }

    public static synchronized CustomPostgresContainer getInstance() {
        if (container == null) {
            container = new CustomPostgresContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", container.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", container.getUsername());
        System.setProperty("TEST_DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }
}
