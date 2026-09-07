local stockKey = KEYS[1]
local purchasedUsersKey = KEYS[2]

local userId = ARGV[1]
local userTtlSeconds = tonumber(ARGV[2])

if redis.call('SISMEMBER', purchasedUsersKey, userId) == 1 then
    return 2
end

local stock = tonumber(redis.call('GET', stockKey))
if not stock or stock <= 0 then
    return 1
end

redis.call('DECR', stockKey)

local added = redis.call('SADD', purchasedUsersKey, userId)
if added == 1 then
    redis.call('EXPIRE', purchasedUsersKey, userTtlSeconds)
end

return 0