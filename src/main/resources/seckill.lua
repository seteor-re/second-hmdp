-- 优惠卷id
local voucherId = ARGV[1]
-- 用户id
local userId = ARGV[2]
-- 订单id
local orderId = ARGV[3]
-- 数据key
-- 库存数据key
local stockKey = 'seckill:stock:' .. voucherId
-- 订单数据key
local orderKey = 'seckill:order:' .. voucherId
-- 业务脚本
-- 判断当前库存是否充足
if(tonumber(redis.call('get',stockKey)) <= 0) then
-- 库存不足返回1
    return 1
end
-- 判断用户下单
if (redis.call('sismember',orderKey,userId)) then
-- 存在说明是重复下单，返回2
    return 2
end
-- 扣减库存
redis.call('incrby',stockKey,-1)
-- 下单
redis.call('sadd',orderKey,userId)
-- 存放到消息队列中
redis.call('xadd','stream.orders','*','userId',userId,'voucherId',voucherId,'id',orderId)
return 0