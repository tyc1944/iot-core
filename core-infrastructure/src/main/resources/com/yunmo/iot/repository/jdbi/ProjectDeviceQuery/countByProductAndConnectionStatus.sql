select count(id) as `count`, device_status as `status`
from device
where project_id = :projectId and product_id = :productId and deleted = false
group by device_status