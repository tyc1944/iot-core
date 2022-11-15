select count(d.id) as `count`, d.device_status as `status`
from device d join project p on d.project_id = p.id
where p.operator_id = :operatorId and product_id = :productId and deleted = false
group by device_status