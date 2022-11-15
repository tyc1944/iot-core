select count(id) as `count`, device_status as `status`
from device
where product_id = :productId and deleted = false
group by device_status