package com.github.christiangda.storm.metrics;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class InfluxDBSenderTest {

    private Map<Object, Object> config = new HashMap<Object, Object>() {{
        put(InfluxDBSender.KEY_INFLUXDB_URL, "http://localhost:8086");
        put(InfluxDBSender.KEY_INFLUXDB_DATABASE, "test-database");
        put(InfluxDBSender.KEY_INFLUXDB_USERNAME, "test-username");
        put(InfluxDBSender.KEY_INFLUXDB_PASSWORD, "test-password");
        put(InfluxDBSender.KEY_INFLUXDB_MEASUREMENT_PREFIX, "test-prefix");
        put(InfluxDBSender.KEY_INFLUXDB_ENABLE_GZIP, true);
    }};

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private Class influxDBSenderClass;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        influxDBSenderClass = InfluxDBSender.class;
    }

    @Test
    public void itShouldAssignAllFieldWhenInvokeConstructorMethodWithFilledConfMap() throws Exception {
        // ----------------------------------------
        // Given


        // ----------------------------------------
        // When
        InfluxDBSender influxDBSender = Mockito.spy(new InfluxDBSender(config));

        Mockito.doNothing().when(influxDBSender).prepareConnection();
        Mockito.doNothing().when(influxDBSender).createDatabaseIfNotExists();

        // ----------------------------------------
        // Then
        Field influxdbUrlField = influxDBSenderClass.getDeclaredField("influxdbUrl");
        influxdbUrlField.setAccessible(true);
        assertNotNull(influxdbUrlField.get(influxDBSender));
        assertEquals(influxdbUrlField.get(influxDBSender), config.get(InfluxDBSender.KEY_INFLUXDB_URL));

        Field influxdbUsernameField = influxDBSenderClass.getDeclaredField("influxdbUsername");
        influxdbUsernameField.setAccessible(true);
        assertNotNull(influxdbUsernameField.get(influxDBSender));
        assertEquals(influxdbUsernameField.get(influxDBSender), config.get(InfluxDBSender.KEY_INFLUXDB_USERNAME));

        Field influxdbPasswordField = influxDBSenderClass.getDeclaredField("influxdbPassword");
        influxdbPasswordField.setAccessible(true);
        assertNotNull(influxdbPasswordField.get(influxDBSender));
        assertEquals(influxdbPasswordField.get(influxDBSender), config.get(InfluxDBSender.KEY_INFLUXDB_PASSWORD));

        Field influxdbDatabaseField = influxDBSenderClass.getDeclaredField("influxdbDatabase");
        influxdbDatabaseField.setAccessible(true);
        assertNotNull(influxdbDatabaseField.get(influxDBSender));
        assertEquals(influxdbDatabaseField.get(influxDBSender), config.get(InfluxDBSender.KEY_INFLUXDB_DATABASE));

        Field influxdbMeasurementPrefixField = influxDBSenderClass.getDeclaredField("influxdbMeasurementPrefix");
        influxdbMeasurementPrefixField.setAccessible(true);
        assertNotNull(influxdbMeasurementPrefixField.get(influxDBSender));
        assertEquals(influxdbMeasurementPrefixField.get(influxDBSender), config.get(InfluxDBSender.KEY_INFLUXDB_MEASUREMENT_PREFIX));

        Field influxdbEnableGzipField = influxDBSenderClass.getDeclaredField("influxdbEnableGzip");
        influxdbEnableGzipField.setAccessible(true);
        assertNotNull(influxdbEnableGzipField.get(influxDBSender));
        assertEquals(influxdbEnableGzipField.get(influxDBSender), config.get(InfluxDBSender.KEY_INFLUXDB_ENABLE_GZIP));
    }

    @Test
    public void itShouldAssignAllFieldWithDefaultValuesWhenInvokeConstructorMethodWithUnFilledConfMap() throws Exception {
        // ----------------------------------------
        // Given
        config = new HashMap<>();

        // ----------------------------------------
        // when our method tested
        InfluxDBSender influxDBSender = Mockito.spy(new InfluxDBSender(config));

        Mockito.doNothing().when(influxDBSender).prepareConnection();
        Mockito.doNothing().when(influxDBSender).createDatabaseIfNotExists();

        // ----------------------------------------
        // Then
        Field influxdbUrlField = influxDBSenderClass.getDeclaredField("influxdbUrl");
        influxdbUrlField.setAccessible(true);
        assertNotNull(influxdbUrlField.get(influxDBSender));
        assertEquals(influxdbUrlField.get(influxDBSender), InfluxDBSender.DEFAULT_INFLUXDB_URL);

        Field influxdbUsernameField = influxDBSenderClass.getDeclaredField("influxdbUsername");
        influxdbUsernameField.setAccessible(true);
        assertNotNull(influxdbUsernameField.get(influxDBSender));
        assertEquals(influxdbUsernameField.get(influxDBSender), InfluxDBSender.DEFAULT_INFLUXDB_USERNAME);

        Field influxdbPasswordField = influxDBSenderClass.getDeclaredField("influxdbPassword");
        influxdbPasswordField.setAccessible(true);
        assertNotNull(influxdbPasswordField.get(influxDBSender));
        assertEquals(influxdbPasswordField.get(influxDBSender), InfluxDBSender.DEFAULT_INFLUXDB_PASSWORD);

        Field influxdbDatabaseField = influxDBSenderClass.getDeclaredField("influxdbDatabase");
        influxdbDatabaseField.setAccessible(true);
        assertNotNull(influxdbDatabaseField.get(influxDBSender));
        assertEquals(influxdbDatabaseField.get(influxDBSender), InfluxDBSender.DEFAULT_INFLUXDB_DATABASE);

        Field influxdbMeasurementPrefixField = influxDBSenderClass.getDeclaredField("influxdbMeasurementPrefix");
        influxdbMeasurementPrefixField.setAccessible(true);
        assertNotNull(influxdbMeasurementPrefixField.get(influxDBSender));
        assertEquals(influxdbMeasurementPrefixField.get(influxDBSender), InfluxDBSender.DEFAULT_INFLUXDB_MEASUREMENT_PREFIX);

        Field influxdbEnableGzipField = influxDBSenderClass.getDeclaredField("influxdbEnableGzip");
        influxdbEnableGzipField.setAccessible(true);
        assertNotNull(influxdbEnableGzipField.get(influxDBSender));
        assertEquals(influxdbEnableGzipField.get(influxDBSender), InfluxDBSender.DEFAULT_INFLUXDB_ENABLE_GZIP);
    }

    @Test
    public void itShouldIOExceptionWhenInvokeCreateDatabaseMethodWithFilledConfMap() throws Exception {
        // ----------------------------------------
        // Given
        final InfluxDBSender influxDBSender = Mockito.spy(new InfluxDBSender(config));
        final InfluxDB influxDB = Mockito.mock(InfluxDB.class);

        final Field fieldInfluxDB = influxDBSenderClass.getDeclaredField("influxDB");
        fieldInfluxDB.setAccessible(true);

        // Inject mocked
        fieldInfluxDB.set(influxDBSender, influxDB);
        Mockito.doThrow(Exception.class).when(influxDB).createDatabase(Mockito.anyString());

        // ----------------------------------------
        // Then
        thrown.expect(Exception.class);

        // ----------------------------------------
        // when our method tested
        influxDBSender.createDatabaseIfNotExists();
    }

    @Test
    public void itShouldAssignNullToInfluxDBAndCloseConnectionWhenInvokeCloseMethod() throws Exception {
        // ----------------------------------------
        // Given
        final InfluxDBSender influxDBSender = Mockito.spy(new InfluxDBSender(config));
        final InfluxDB influxDB = Mockito.mock(InfluxDB.class);

        final Field fieldInfluxDB = influxDBSenderClass.getDeclaredField("influxDB");
        fieldInfluxDB.setAccessible(true);

        // Inject mocked
        fieldInfluxDB.set(influxDBSender, influxDB);

        Mockito.doNothing().when(influxDB).close();


        // ----------------------------------------
        // when our method tested
        influxDBSender.closeConnection();

        // ----------------------------------------
        // Then
        Mockito.verify(influxDBSender, Mockito.times(1)).closeConnection();
        Mockito.verify(influxDB, Mockito.times(1)).close();

        fieldInfluxDB.get(influxDBSender);
    }


    @Test
    public void itShouldCallInfluxDBWriteMethodWhenInvokeSendMethod() throws Exception {
        // ----------------------------------------
        // Given
        final InfluxDB influxDB = Mockito.mock(InfluxDB.class);
        final BatchPoints batchPoints = Mockito.mock(org.influxdb.dto.BatchPoints.class);

        final InfluxDBSender influxDBSender = Mockito.spy(new InfluxDBSender(config));

        final Field fieldInfluxDB = influxDBSenderClass.getDeclaredField("influxDB");
        fieldInfluxDB.setAccessible(true);

        final Field fieldBatchPoints = influxDBSenderClass.getDeclaredField("batchPoints");
        fieldBatchPoints.setAccessible(true);

        // Inject mocked
        fieldInfluxDB.set(influxDBSender, influxDB);
        fieldBatchPoints.set(influxDBSender, batchPoints);

        Mockito.doNothing().when(influxDB).write(batchPoints);

        // ----------------------------------------
        // when our method tested
        influxDBSender.sendPoints();

        // ----------------------------------------
        // Then
        Mockito.verify(influxDBSender, Mockito.times(1)).sendPoints();
        Mockito.verify(influxDB, Mockito.times(1)).write(batchPoints);
    }

    @Test
    public void itShouldCallInfluxDBCreateDatabaseMethodWhenInvokeSendMethod() throws Exception {
        // ----------------------------------------
        // Given
        final InfluxDB influxDB = Mockito.mock(InfluxDB.class);
        final InfluxDBSender influxDBSender = Mockito.spy(new InfluxDBSender(config));

        final Field fieldInfluxDB = influxDBSenderClass.getDeclaredField("influxDB");
        fieldInfluxDB.setAccessible(true);

        // Inject mocked
        fieldInfluxDB.set(influxDBSender, influxDB);

        Mockito.doNothing().when(influxDB).createDatabase(Mockito.anyString());

        // ----------------------------------------
        // when our method tested
        influxDBSender.createDatabaseIfNotExists();

        // ----------------------------------------
        // Then
        Mockito.verify(influxDBSender, Mockito.times(1)).createDatabaseIfNotExists();
        Mockito.verify(influxDB, Mockito.times(1)).createDatabase(Mockito.anyString());
    }
}