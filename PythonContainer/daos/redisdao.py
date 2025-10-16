import aioredis
import os

async def create_redis_pool():
  global redis 
  redis = await aioredis.create_redis_pool(f'redis://{os.environ["STATE_HOST"]}:{os.environ["STATE_PORT"]}')

async def open_connection(username, holder):
  await redis.hset("online_users", username, holder)

async def notify_online(holder):
  await redis.hset("online_servers", holder, "online")
  await redis.expire("online_servers", 60)

async def close_connection(username):
  await redis.hdel("online_users", username)

async def get_online_servers():
  on_servers = await redis.hgetall("online_servers")
  return list(on_servers.keys())

async def remove_serer_from_redis(holder):
  await redis.hdel("online_servers", holder)