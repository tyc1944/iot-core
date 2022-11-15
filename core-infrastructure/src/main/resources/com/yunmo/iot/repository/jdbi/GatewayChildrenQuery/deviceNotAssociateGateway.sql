SELECT d.*
FROM  device d left join gateway_association ga on ga.device_id = d.id
where ga.device_id is null and d.project_id = :projectId and (d.gateway = false or d.gateway is null) and d.deleted = false