package com.github.christiangda.storm.metrics;

import org.apache.storm.Config;
import org.apache.storm.metric.api.IMetricsConsumer;
import org.apache.storm.task.IErrorReporter;
import org.apache.storm.task.TopologyContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyMap;

@SuppressWarnings("unchecked")
public class InfluxDBMetricsConsumerTest {

    private static final String STORM_TOPOLOGY_NAME = "Some-Storm-Topology-Name-12-3456789101";

    private InfluxDBMetricsConsumer influxDBMetricsConsumer;
    private Class influxDBMetricsConsumerClass;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        influxDBMetricsConsumer = new InfluxDBMetricsConsumer();
        influxDBMetricsConsumerClass = InfluxDBMetricsConsumer.class;
    }

    @Test
    public void itShouldParseAllParametersWithAllParametersGood() throws Exception {
        // ----------------------------------------
        // Given
        final Map<String, String> stormConfig = new HashMap();
        final Map<String, Object> registrationArgument = new HashMap();

        Map<Object, Object> capturedArguments;

        stormConfig.put(Config.TOPOLOGY_NAME, STORM_TOPOLOGY_NAME);
        registrationArgument.put(InfluxDBSender.KEY_INFLUXDB_URL, InfluxDBSender.KEY_INFLUXDB_URL);
        registrationArgument.put(InfluxDBSender.KEY_INFLUXDB_USERNAME, InfluxDBSender.KEY_INFLUXDB_USERNAME);
        registrationArgument.put(InfluxDBSender.KEY_INFLUXDB_PASSWORD, InfluxDBSender.KEY_INFLUXDB_PASSWORD);
        registrationArgument.put(InfluxDBSender.KEY_INFLUXDB_DATABASE, InfluxDBSender.KEY_INFLUXDB_DATABASE);
        registrationArgument.put(InfluxDBSender.KEY_INFLUXDB_MEASUREMENT_PREFIX, InfluxDBSender.KEY_INFLUXDB_MEASUREMENT_PREFIX);
        registrationArgument.put(InfluxDBSender.KEY_INFLUXDB_ENABLE_GZIP, InfluxDBSender.KEY_INFLUXDB_ENABLE_GZIP);

        final TopologyContext topologyContext = Mockito.mock(TopologyContext.class);
        final IErrorReporter errorReporter = Mockito.mock(IErrorReporter.class);
        final InfluxDBSender influxDBSender = Mockito.mock(InfluxDBSender.class);

        // Necessary to control its methods
        final InfluxDBMetricsConsumer influxDBMetricsConsumer = Mockito.spy(new InfluxDBMetricsConsumer());

        ArgumentCaptor<Map> configMapCaptor = ArgumentCaptor.forClass(Map.class);

        // Inject my mockedInfluxDBSender and get the arguments to verify it
        Mockito.doReturn(influxDBSender).when(influxDBMetricsConsumer).makeInfluxDBSender(configMapCaptor.capture());

        // ----------------------------------------
        // When execute the method under test
        influxDBMetricsConsumer.prepare(stormConfig, registrationArgument, topologyContext, errorReporter);

        // ----------------------------------------
        // Then validate
        Mockito.verify(influxDBMetricsConsumer, Mockito.times(1)).makeInfluxDBSender(anyMap());

        // Returned Tags from method
        capturedArguments = configMapCaptor.getValue();

        // validate that all config sent was received
        assertTrue(capturedArguments.containsKey(Config.TOPOLOGY_NAME));
        assertTrue(capturedArguments.containsKey(InfluxDBSender.KEY_INFLUXDB_URL));
        assertTrue(capturedArguments.containsKey(InfluxDBSender.KEY_INFLUXDB_USERNAME));
        assertTrue(capturedArguments.containsKey(InfluxDBSender.KEY_INFLUXDB_PASSWORD));
        assertTrue(capturedArguments.containsKey(InfluxDBSender.KEY_INFLUXDB_DATABASE));
        assertTrue(capturedArguments.containsKey(InfluxDBSender.KEY_INFLUXDB_MEASUREMENT_PREFIX));
        assertTrue(capturedArguments.containsKey(InfluxDBSender.KEY_INFLUXDB_ENABLE_GZIP));
    }

    @Test
    public void testHandleDataPointsMethod() throws NoSuchFieldException, IllegalAccessException {
        // ----------------------------------------
        // Given
        Map<String, String> actualTags;
        Map<String, Object> actualFields;

        final Map<String, String> expectedTags = new HashMap<>();
        final Map<String, Object> expectedFlieds = new HashMap<>();

        expectedTags.put("Topology", STORM_TOPOLOGY_NAME);
        expectedTags.put("ComponentId", "my-component-id");

        expectedFlieds.put("TaskId", "1");
        expectedFlieds.put("WorkerHost", "my-worker-host");
        expectedFlieds.put("WorkerPort", "12345");
        expectedFlieds.put("Timestamp", "123456789");
        expectedFlieds.put("UpdateIntervalSecs", "5");

        final IMetricsConsumer.TaskInfo taskInfo;
        final Collection<IMetricsConsumer.DataPoint> dataPoints = new ArrayList<>();

        taskInfo = new IMetricsConsumer.TaskInfo(
                (String) expectedFlieds.get("WorkerHost"),
                Integer.valueOf(expectedFlieds.get("WorkerPort").toString()),
                expectedTags.get("ComponentId"),
                Integer.valueOf(expectedFlieds.get("TaskId").toString()),
                Long.valueOf(expectedFlieds.get("Timestamp").toString()),
                Integer.valueOf(expectedFlieds.get("UpdateIntervalSecs").toString())
        );

        dataPoints.add(new IMetricsConsumer.DataPoint("test-string", "string"));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-double", 1234.5d));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-integer", 10));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-long", 10000000L));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-float", 20000.0f));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-boolean", true));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-number", 123456));
        dataPoints.add(new IMetricsConsumer.DataPoint("test-null", null));

        // Test map with the same data types
        dataPoints.add(new IMetricsConsumer.DataPoint("test-map", dataPoints));


        // Mock spy object
        final InfluxDBSender influxDBSender = Mockito.mock(InfluxDBSender.class);

        // Prepare Captors
        ArgumentCaptor<Map> setTagsArguments = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> setFieldsArguments = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> processDataPointArgumentsFirst = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Objects> processDataPointArgumentsSecond = ArgumentCaptor.forClass(Objects.class);

        // Intercept influxDBSender field inside influxDBMetricsConsumer and inject our mock Object
        final Field fieldInfluxDBSender = influxDBMetricsConsumerClass.getDeclaredField("influxDBSender");
        fieldInfluxDBSender.setAccessible(true);
        fieldInfluxDBSender.set(influxDBMetricsConsumer, influxDBSender);

        final Field fieldTopolyName = influxDBMetricsConsumerClass.getDeclaredField("topologyName");
        fieldTopolyName.setAccessible(true);
        fieldTopolyName.set(influxDBMetricsConsumer, STORM_TOPOLOGY_NAME);

        // Define rules for our injected object
        Mockito.doNothing().when(influxDBSender).setTags(setTagsArguments.capture());
        Mockito.doNothing().when(influxDBSender).setFields(setFieldsArguments.capture());
        Mockito.doNothing().when(influxDBSender).prepareDataPoint(
                processDataPointArgumentsFirst.capture(),
                processDataPointArgumentsSecond.capture()
        );
        Mockito.doNothing().when(influxDBSender).sendPoints();

        // ----------------------------------------
        // when our method tested
        influxDBMetricsConsumer.handleDataPoints(taskInfo, dataPoints);

        // ----------------------------------------
        // Then
        Mockito.verify(influxDBSender, Mockito.times(1)).setTags(Mockito.<String, String>anyMap());
        Mockito.verify(influxDBSender, Mockito.atLeastOnce()).prepareDataPoint(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(influxDBSender, Mockito.times(1)).sendPoints();

        // Returned Tags from method
        actualTags = setTagsArguments.getValue();
        actualFields = setFieldsArguments.getValue();

        //1. Test equal, ignore order
        assertThat(actualTags, is(expectedTags));
        assertThat(actualFields, is(expectedFlieds));

        //2. Test size
        assertThat(actualTags.size(), is(expectedTags.size()));
        assertThat(expectedFlieds.size(), is(expectedFlieds.size()));
    }

    @Test
    public void itShouldCallInfluxDBSenderPrepareDataPointMethodWhenValueIsAnyType() throws NoSuchFieldException, IllegalAccessException {
        int recursionRound = 0;
        // ----------------------------------------
        // Given
        final InfluxDBSender influxDBSender = Mockito.mock(InfluxDBSender.class);

        // Intercept influxDBSender field inside influxDBMetricsConsumer and inject our mock Object
        final Field fieldInfluxDBSender = influxDBMetricsConsumerClass.getDeclaredField("influxDBSender");
        fieldInfluxDBSender.setAccessible(true);
        fieldInfluxDBSender.set(influxDBMetricsConsumer, influxDBSender);

        // ----------------------------------------
        // when our method tested
        influxDBMetricsConsumer.processDataPoint("test-string", "string", recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-string", "string");

        influxDBMetricsConsumer.processDataPoint("test-double", 1234.5d, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-double", 1234.5d);

        influxDBMetricsConsumer.processDataPoint("test-integer", 10, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-integer", 10);

        influxDBMetricsConsumer.processDataPoint("test-long", 10000000L, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-long", 10000000L);

        influxDBMetricsConsumer.processDataPoint("test-float", 20000.0f, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-float", 20000.0f);

        influxDBMetricsConsumer.processDataPoint("test-boolean", true, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-boolean", true);

        influxDBMetricsConsumer.processDataPoint("test-number", 123456, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(1)).prepareDataPoint("test-number", 123456);
    }

    @Test
    public void itShouldCallInfluxDBSenderPrepareDataPointMethodWhenValueIsMap() throws NoSuchFieldException, IllegalAccessException {
        int recursionRound = 0;
        // ----------------------------------------
        // Given
        final InfluxDBSender influxDBSender = Mockito.mock(InfluxDBSender.class);
        final Map<String, Object> dataPoints = new HashMap<>();

        dataPoints.put("test-string", "string");
        dataPoints.put("test-double", 1234.5d);
        dataPoints.put("test-integer", 10);
        dataPoints.put("test-long", 10000000L);
        dataPoints.put("test-float", 20000.0f);
        dataPoints.put("test-boolean", true);
        dataPoints.put("test-number", 123456);

        // Intercept influxDBSender field inside influxDBMetricsConsumer and inject our mock Object
        final Field fieldInfluxDBSender = influxDBMetricsConsumerClass.getDeclaredField("influxDBSender");
        fieldInfluxDBSender.setAccessible(true);
        fieldInfluxDBSender.set(influxDBMetricsConsumer, influxDBSender);

        // ----------------------------------------
        // when our method tested
        influxDBMetricsConsumer.processDataPoint("test-collection", dataPoints, recursionRound);
        Mockito.verify(influxDBSender, Mockito.times(7)).prepareDataPoint(anyString(), any());
    }

    @Test
    public void itShouldAssignNullToInfluxDBSenderInCleanupMethod() throws NoSuchFieldException, IllegalAccessException {
        // ----------------------------------------
        // Given
        final InfluxDBSender influxDBSender = Mockito.mock(InfluxDBSender.class);

        // Intercept influxDBSender field inside influxDBMetricsConsumer and inject our mock Object
        final Field fieldInfluxDBSender = influxDBMetricsConsumerClass.getDeclaredField("influxDBSender");
        fieldInfluxDBSender.setAccessible(true);
        fieldInfluxDBSender.set(influxDBMetricsConsumer, influxDBSender);

        // ----------------------------------------
        // when our method tested
        influxDBMetricsConsumer.cleanup();

        // ----------------------------------------
        // Then
        Mockito.verify(influxDBSender, Mockito.times(1)).closeConnection();
        assertNull(fieldInfluxDBSender.get(influxDBMetricsConsumer));
    }


    @Test
    public void itShouldReturnInfluxDBSenderObjectWhenMakeInfluxDBSenderMethodIsCalled() throws Exception {
        // ----------------------------------------
        // Given

        final InfluxDBMetricsConsumer influxDBMetricsConsumer = Mockito.mock(InfluxDBMetricsConsumer.class);
        final InfluxDBSender influxDBSender = Mockito.mock(InfluxDBSender.class);

        Mockito.doReturn(influxDBSender).when(influxDBMetricsConsumer).makeInfluxDBSender(anyMap());

        // ----------------------------------------
        // when our method tested
        influxDBMetricsConsumer.makeInfluxDBSender(anyMap());

        // ----------------------------------------
        // Then
        Mockito.verify(influxDBMetricsConsumer, Mockito.times(1)).makeInfluxDBSender(anyMap());
        assertEquals(influxDBSender, influxDBMetricsConsumer.makeInfluxDBSender(anyMap()));
    }

}