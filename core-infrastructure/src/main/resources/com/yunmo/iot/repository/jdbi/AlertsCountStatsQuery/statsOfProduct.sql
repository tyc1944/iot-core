SELECT r.alert_type as `type`,count(*) as `count`
FROM  rule_alert r
where r.alert_status = 'TRIGGER' AND r.product_id = :productId
  AND (:timeStart is null or r.alert_time >= :timeStart)
  AND (:timeEnd is null or r.alert_time \<= :timeEnd)
group by r.alert_type