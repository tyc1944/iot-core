CREATE  KEYSPACE iot  WITH REPLICATION ={
   'class' : 'NetworkTopologyStrategy',
   'dc0' : 1
  };

CREATE TABLE iot.telemetry (
    device_id bigint,
    schema_name text,
    event_time timestamp,
    message blob,
    PRIMARY KEY ((device_id, schema_name), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC)
and compaction = { 'class' : 'org.apache.cassandra.db.compaction.TimeWindowCompactionStrategy', 'compaction_window_size' : 1, 'compaction_window_unit' : 'DAYS', 'max_threshold' : 32, 'min_threshold' : 4 };