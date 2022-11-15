create database if not exists  iot ON CLUSTER default_cluster;

CREATE TABLE IF NOT EXISTS iot.connect_log ON CLUSTER default_cluster (
 `projectId` Int64,
  `deviceId` Int64,
 `eventTime` Datetime('Asia/Shanghai') Codec(DoubleDelta, LZ4),
 online UInt8
 )  ENGINE = ReplicatedMergeTree('/clickhouse/iot/tables/{shard}/connect_log', '{replica}')
 ORDER BY (`deviceId`,`eventTime`);

CREATE MATERIALIZED VIEW IF NOT EXISTS iot.connect_log_hourly_mv  ON CLUSTER default_cluster
 ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/iot/tables/{shard}/connect_log_hourly_mv', '{replica}')
ORDER BY (projectId,hourlyTime)  AS
SELECT
  projectId,
  toStartOfHour(eventTime) AS hourlyTime,
  sumState(online) AS online,
  countState() as total
FROM iot.connect_log
GROUP BY projectId,hourlyTime;

select hourlyTime,sumMerge(online) as online, countMerge(total) - online as offline from iot.connect_log_hourly_mv GROUP BY projectId,hourlyTime;
