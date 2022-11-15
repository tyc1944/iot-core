SELECT r.alert_type as `type`,count(*) as `count`
FROM  rule_alert r
where r.alert_status = 'TRIGGER' AND r.project_id = :projectId
  AND (:timeStart is null or r.alert_time >= :timeStart)
  AND (:timeEnd is null or r.alert_time \<= :timeEnd)
group by r.alert_type