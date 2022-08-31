local key = KEYS[1]

local purchase_order_id = ARGV[1]
local current_stock = tonumber(ARGV[2])
local purchase_amount = tonumber(ARGV[3])
local current_time = tonumber(ARGV[4])
local ttl = tonumber(ARGV[5])

local remained_stock = 0

local stock_count_field = "stock_count"
local purchase_order_set_key = "z"..key
local product_stock_hash_key = "h"..key

local is_stock_exist = redis.call("hexists", product_stock_hash_key, stock_count_field)

if is_stock_exist == 0 then
    local deductStock = current_stock - purchase_amount
    if deductStock >= 0 then
        remained_stock = deductStock
    else
        return nil
    end

    local value = "purchase_order_id:"..purchase_order_id
    redis.call("zadd", purchase_order_set_key, current_time + ttl, value)
    redis.call("hset", product_stock_hash_key, stock_count_field, remained_stock)
else
    local recent_stock = redis.call("hget", product_stock_hash_key, stock_count_field)
    remained_stock = recent_stock - purchase_amount

    local value = "purchase_order_id:"..purchase_order_id
    redis.call("zadd", purchase_order_set_key, current_time + ttl, value)
    redis.call("hset", product_stock_hash_key, stock_count_field, remained_stock)
end

return remained_stock
