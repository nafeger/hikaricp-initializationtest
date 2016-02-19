package com.nate_land.hikarcp_test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by nfeger on 2/19/16.
 */
public class HikariQuickTest {

    /**
     * Why you fail?
     *
     */
    @Test
    public void testUseConnectionImmedately() throws SQLException {
        try (HikariDataSource ds = new HikariDataSource()) {
            ds.setAutoCommit(true);
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTimeout(5000);
            ds.setConnectionTestQuery("select 1");
            ds.setDriverClassName("org.h2.Driver");
            ds.setJdbcUrl("jdbc:h2:mem:DbTest-" + UUID.randomUUID() + ";user=sa");
            ds.addDataSourceProperty("loginTimeout", "10");

            try (Connection connection = ds.getConnection()) {
                Connection unwrap = connection.unwrap(Connection.class);
                try (PreparedStatement statement = connection.prepareStatement("select 1")) {
                    assertThat(statement.getQueryTimeout()).isEqualTo(5);
                }
                connection.setAutoCommit(false);
                connection.close();

               assertThat(unwrap.getAutoCommit()).isEqualTo(true);
            }
        }
    }

    /**
     * Why you no fail?
     *
     */
    @Test
    public void testUseConnectionWithSleep() throws SQLException, InterruptedException {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(true);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(5000);
        config.setConnectionTestQuery("select 1");
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:DbTest-" + UUID.randomUUID() + ";user=sa");
        config.addDataSourceProperty("loginTimeout", "10");


        try (HikariDataSource ds = new HikariDataSource(config)) {
            Thread.sleep(1000);

            try (Connection connection = ds.getConnection()) {
                Connection unwrap = connection.unwrap(Connection.class);
                try (PreparedStatement statement = connection.prepareStatement("select 1")) {
                    assertThat(statement.getQueryTimeout()).isEqualTo(5);
                }
                connection.setAutoCommit(false);
                connection.close();

                assertThat(unwrap.getAutoCommit()).isEqualTo(true);
            }
        }
    }
}
