-- 比较线程标识与锁的标识是否一致
if(redis.call('get',KEY[1] == ARGV[1])) then
    return redis.call('del',KEY[1])
end
return 0
