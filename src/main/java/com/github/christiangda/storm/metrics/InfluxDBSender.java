package com.github.christiangda.storm.metrics;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 *
 */
class InfluxDBSender {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBSender.class);

    // Configurations keys
    public static final String KEY_INFLUXDB_URL = "metrics.influxdb.url";
    public static final String KEY_INFLUXDB_USERNAME = "metrics.influxdb.username";
    public static final String KEY_INFLUXDB_PASSWORD = "metrics.influxdb.password";
    public static final String KEY_INFLUXDB_DATABASE = "metrics.influxdb.database";
    public static final String KEY_INFLUXDB_MEASUREMENT_PREFIX = "metrics.influxdb.measurement.prefix";
    public static final String KEY_INFLUXDB_ENABLE_GZIP = "metrics.influxdb.enable.gzip";

    // Default config values for non requires
    public static final String DEFAULT_INFLUXDB_URL = "http://localhost:8089";
    public static final String DEFAULT_INFLUXDB_USERNAME = ""; //empty
    public static final String DEFAULT_INFLUXDB_PASSWORD = ""; //empty
    public static final String DEFAULT_INFLUXDB_DATABASE = "apache-storm-metrics";
    public static final String DEFAULT_INFLUXDB_MEASUREMENT_PREFIX = "storm-";
    public static final Boolean DEFAULT_INFLUXDB_ENABLE_GZIP = true;

    private InfluxDB influxDB;
    private BatchPoints batchPoints;
    private String influxdbUrl;
    private String influxdbUsername;
    private String influxdbPassword;
    private String influxdbDatabase;
    private String influxdbMeasurementPrefix;
    private Boolean influxdbEnableGzip;

    // Flags
    private boolean databaseWasCreated = false;
    private Map<String, Object> fields;
    private Map<String, String> tags;

    public InfluxDBSender(Map<Object, Object> config) {

        LOG.debug("{}: config = {}", this.getClass().getSimpleName(), config.toString());

        this.influxdbUrl = (String) getKeyValueOrDefaultValue(config, KEY_INFLUXDB_URL, DEFAULT_INFLUXDB_URL);
        this.influxdbUsername = (String) getKeyValueOrDefaultValue(config, KEY_INFLUXDB_USERNAME, DEFAULT_INFLUXDB_USERNAME);
        this.influxdbPassword = (String) getKeyValueOrDefaultValue(config, KEY_INFLUXDB_PASSWORD, DEFAULT_INFLUXDB_PASSWORD);
        this.influxdbDatabase = (String) getKeyValueOrDefaultValue(config, KEY_INFLUXDB_DATABASE, DEFAULT_INFLUXDB_DATABASE);
        this.influxdbMeasurementPrefix = (String) getKeyValueOrDefaultValue(config, KEY_INFLUXDB_MEASUREMENT_PREFIX, DEFAULT_INFLUXDB_MEASUREMENT_PREFIX);
        this.influxdbEnableGzip = (Boolean) getKeyValueOrDefaultValue(config, KEY_INFLUXDB_ENABLE_GZIP, DEFAULT_INFLUXDB_ENABLE_GZIP);
        this.prepareConnection();
    }

    /**
     * Look at the object collection if key exist, if not, it return defaultValue
     *
     * @param objects      Collection of Object
     * @param key          Key to lookup at objects collections
     * @param defaultValue default value to be returned
     * @return Object
     */
    private Object getKeyValueOrDefaultValue(Map<Object, Object> objects, String key, Object defaultValue) {
        if (objects.containsKey(key)) {
            return objects.get(key);
        } else {
            LOG.warn("{}: Using default parameter for {}", this.getClass().getSimpleName(), key);
            return defaultValue;
        }
    }

    /**
     * Prepare connection pool to InfluxDB server
     */
    public void prepareConnection() {
        if (influxDB == null) {
            LOG.debug("{}: Preparing connection to InfluxDB: [ url='{}', username='{}', password='{}' ]",
                    this.getClass().getSimpleName(),
                    this.influxdbUrl,
                    this.influxdbUsername,
                    this.influxdbPassword
            );

            if (this.influxdbUsername.isEmpty() && this.influxdbPassword.isEmpty()) {
                this.influxDB = InfluxDBFactory.connect(this.influxdbUrl);
            } else {
                this.influxDB = InfluxDBFactory.connect(this.influxdbUrl, this.influxdbUsername, this.influxdbPassword);
            }

            // additional connections options
            if (this.influxdbEnableGzip) {
                this.influxDB.enableGzip();
            }
        } else {
            LOG.debug("{}: InfluxDB connection was available: [ url='{}', username='{}', password='{}' ]",
                    this.getClass().getSimpleName(),
                    this.influxdbUrl,
                    this.influxdbUsername,
                    this.influxdbPassword
            );
        }
    }

    /**
     * Create the database if not exist
     */
    void createDatabaseIfNotExists() {
        if (!this.databaseWasCreated) {

            LOG.debug("{}: Creating database with name = {}", this.getClass().getSimpleName(), this.influxdbDatabase);

            this.influxDB.createDatabase(this.influxdbDatabase);
            this.databaseWasCreated = true;
        }
    }

    /**
     * Create a BatchPoints
     */
    void prepareBatchPoints() {
        this.batchPoints = BatchPoints
                .database(this.influxdbDatabase)
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

    /**
     * Prepare InfluxDB dataPoint to be send
     *
     * @param name  dataPoint name
     * @param value dataPoint value
     */
    public void prepareDataPoint(String name, Object value) {

        if (this.batchPoints == null) {
            this.prepareBatchPoints();
        }

        final String measurement = this.influxdbMeasurementPrefix + name;

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}: DataPoint name={} has value type={}", this.getClass().getSimpleName(), name, value.getClass().getName());
        }

        if (value instanceof String) {
            Point point = Point.measurement(measurement)
                    .addField("value", (String) value)
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else if (value instanceof Float) {
            Point point = Point.measurement(measurement)
                    .addField("value", (Float) value)
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else if (value instanceof Integer) {
            Point point = Point.measurement(measurement)
                    .addField("value", (Integer) value)
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else if (value instanceof Boolean) {
            Point point = Point.measurement(measurement)
                    .addField("value", (Boolean) value)
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else if (value instanceof Long) {
            Point point = Point.measurement(measurement)
                    .addField("value", ((Long) value).floatValue())
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else if (value instanceof Double) {
            Point point = Point.measurement(measurement)
                    .addField("value", ((Double) value).floatValue())
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else if (value instanceof Number) {
            Point point = Point.measurement(measurement)
                    .addField("value", ((Number) value).floatValue())
                    .fields(this.fields)
                    .tag(this.tags)
                    .build();
            this.batchPoints.point(point);
        } else {
            LOG.warn("{}: Unable to parse the Java type of 'value' : [type:'{}' value:'{}']",
                    this.getClass().getSimpleName(),
                    name,
                    value.getClass().getSimpleName()
            );
        }
    }

    /**
     * Send Points to InfluxDB server
     */
    public void sendPoints() {

        this.createDatabaseIfNotExists();

        if (this.batchPoints != null) {

            LOG.debug("{}: Sending points to database = {}", this.getClass().getSimpleName(), this.influxdbDatabase);

            this.influxDB.write(this.batchPoints);
            this.batchPoints = null;
        } else {
            LOG.warn("No points values to send");
        }
    }

    /**
     * Close connection to InfluxDB server
     */
    public void closeConnection() {

        LOG.debug("{}: Closing connection to database = {}", this.getClass().getSimpleName(), this.influxdbDatabase);

        this.influxDB.close();
    }

    /**
     * Assign the field for every dataPoint.
     *
     * @param fields
     */
    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    /**
     * Assign the tags for every dataPoint.
     *
     * @param tags
     */
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
