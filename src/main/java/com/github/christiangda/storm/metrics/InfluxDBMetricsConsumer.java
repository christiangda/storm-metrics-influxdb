package com.github.christiangda.storm.metrics;

import org.apache.storm.Config;
import org.apache.storm.metric.api.IMetricsConsumer;
import org.apache.storm.task.IErrorReporter;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Forwards all Apache Storm's build-in metrics to a InfluxDB server.
 * <p>
 * Apache Storm has two ways for implementing a Metrics Collector,
 * the first is setting in your topology configuration a Metrics Collector, and
 * the second is configure Apache Storm cluster for use a Metrics Collector for all Topologies.
 *
 * If you want to use the first way to set a Metrics Collector in your Topology, you need to put that
 * in your Topology Configuration:
 * </p>
 * <pre>
 * {@code
 *   Config topologyConf = new Config();
 *
 *   ...
 *
 *   topologyConf.registerMetricsConsumer(com.github.christiangda.storm.InfluxDBMetricsConsumer.class, 1);
 *   topologyConf.put("metrics.reporter.name", "InfluxDBMetricsConsumer");
 *   topologyConf.put("metrics.influxdb.url", "<http://YOUR_INFLUXDB_HOSTNAME:PORT>");
 *   topologyConf.put("metrics.influxdb.username", "<YOUR_INFLUXDB_USERNAME>");
 *   topologyConf.put("metrics.influxdb.password", "<YOUR_INFLUXDB_PASSWORD>");
 *   topologyConf.put("metrics.influxdb.database", "<YOUR_INFLUXDB_DATABASE>");
 *   topologyConf.put("metrics.influxdb.measurement.prefix", "<YOUR_INFLUXDB_MEASUREMENT_PREFIX>");
 *   topologyConf.put("metrics.influxdb.enable.gzip", "<true or false>");
 *
 *   ...
 * }
 * </pre>
 * <p>
 * If you want to use the second way to set a Metrics Collector in your Apache Storm Cluster, you need to put
 * that in your storm.yaml file
 * </p>
 * <pre>
 * {@code
 *  topology.metrics.consumer.register:
 *    - class: "InfluxDBMetricsConsumer.class"
 *      parallelism.hint: 1
 *      argument:
 *        metrics.reporter.name: "InfluxDBMetricsConsumer"
 *        metrics.influxdb.url: "<http://YOUR_INFLUXDB_HOSTNAME:PORT>"
 *        metrics.influxdb.username: "<YOUR_INFLUXDB_USERNAME>"
 *        metrics.influxdb.password: "<YOUR_INFLUXDB_PASSWORD>"
 *        metrics.influxdb.database: "<YOUR_INFLUXDB_DATABASE>"
 *        metrics.influxdb.measurement.prefix: "<YOUR_INFLUXDB_MEASUREMENT_PREFIX>"
 *        metrics.influxdb.enable.gzip: "<true or false>"
 * }
 * </pre>
 */
public class InfluxDBMetricsConsumer implements IMetricsConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBMetricsConsumer.class);

    private static final int MAX_RECURSION_TIMES = 3;

    private InfluxDBSender influxDBSender;
    private String topologyName;

    @Override
    public void prepare(Map stormConf, Object registrationArgument, TopologyContext context, IErrorReporter errorReporter) {

        final Map<Object, Object> mergedConf = new HashMap<>();

        if (stormConf != null && stormConf.size() > 0) {

            LOG.debug("{}: Argument stormConf: {}", this.getClass().getSimpleName(), stormConf.toString());

            this.topologyName = (String) stormConf.get(Config.TOPOLOGY_NAME);
            mergedConf.putAll(stormConf);
        } else {
            LOG.warn("{}: Argument stormConf is Empty or null", this.getClass().getSimpleName());
        }

        if (registrationArgument != null && registrationArgument instanceof Map && ((Map) registrationArgument).size() > 0) {

            LOG.debug("{}: Argument registrationArgument: {}", this.getClass().getSimpleName(), registrationArgument.toString());

            mergedConf.putAll((Map) registrationArgument);
        } else {
            LOG.warn("{}: Argument registrationArgument is Empty or null", this.getClass().getSimpleName());
        }

        this.influxDBSender = makeInfluxDBSender(mergedConf);
    }

    @Override
    public void handleDataPoints(TaskInfo taskInfo, Collection<DataPoint> dataPoints) {

        int recursionRound = 0;

        // Necessary for the topology to continue working when InfluxDB is off-line
        try {
            this.influxDBSender.prepareConnection();

            @SuppressWarnings("unchecked")
            final Map<String, String> tags = new HashMap();

            @SuppressWarnings("unchecked")
            final Map<String, Object> fields = new HashMap();

            // InfluxDB tags are like a field but indexed
            tags.put("ComponentId", taskInfo.srcComponentId);
            tags.put("Topology", this.topologyName);

            // InfluxDB fields per each data point
            fields.put("Timestamp", String.valueOf(taskInfo.timestamp));
            fields.put("UpdateIntervalSecs", String.valueOf(taskInfo.updateIntervalSecs));
            fields.put("TaskId", String.valueOf(taskInfo.srcTaskId));
            fields.put("WorkerHost", taskInfo.srcWorkerHost);
            fields.put("WorkerPort", String.valueOf(taskInfo.srcWorkerPort));

            // sendPoints data to InfluxDB
            this.influxDBSender.setTags(tags);
            this.influxDBSender.setFields(fields);

            // Prepare data to be parse
            for (DataPoint dataPoint : dataPoints) {
                if (dataPoint.value == null) {
                    LOG.warn("{}: Discarding dataPoint: {}, value is null", this.getClass().getSimpleName(), dataPoint.name);
                } else {
                    this.processDataPoint(dataPoint.name, dataPoint.value, recursionRound);
                }
            }
            this.influxDBSender.sendPoints();
            this.influxDBSender.closeConnection();
        } catch (Exception e) {
            LOG.warn("{}: Loss connection to InfluxDB server!, the collected data will be lost. Exception = {}", this.getClass().getSimpleName(), e);
        }
    }

    /**
     * Verify if DataPoint type is a Map and decompose it using recursion,
     * then send it to influxDBSender.
     *
     * @param name  dataPoint name
     * @param value dataPoint value
     */
    public void processDataPoint(String name, Object value, int recursionRound) {

        int recursion_times = (recursionRound > 0) ? recursionRound : 0;

        if (recursion_times <= MAX_RECURSION_TIMES) {
            if (value instanceof String
                    || value instanceof Float
                    || value instanceof Integer
                    || value instanceof Boolean
                    || value instanceof Long
                    || value instanceof Double
                    || value instanceof Number) {

                LOG.debug("{}: Processing dataPoint: [ name: '{}', value: '{}' ]", this.getClass().getSimpleName(), name, value);

                this.influxDBSender.prepareDataPoint(name, value);
            } else if (value instanceof Map) {

                LOG.debug("{}: Processing dataPoint<Map> ...", this.getClass().getSimpleName());

                recursion_times += 1;

                @SuppressWarnings("unchecked")
                Map<String, Object> values = (Map<String, Object>) value;

                for (Map.Entry<String, Object> entry : values.entrySet()) {

                    LOG.debug("{}: ... Processing Map dataPoint entry: [ name: '{}', value: '{}' ]", this.getClass().getSimpleName(), entry.getKey(), entry.getValue());

                    this.processDataPoint(entry.getKey(), entry.getValue(), recursion_times);
                }
            }
        } else {
            LOG.warn("{}: Too Many Nested values in DataPoint named = {}", this.getClass().getSimpleName(), name);
        }
    }

    @Override
    public void cleanup() {
        this.influxDBSender.closeConnection();
        this.influxDBSender = null;
    }

    /**
     * Factory for InfluxDBSender
     *
     * @param config map
     * @return InfluxDBSender new Instance
     */
    InfluxDBSender makeInfluxDBSender(Map<Object, Object> config) {
        return new InfluxDBSender(config);
    }
}
