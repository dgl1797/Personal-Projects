import motor.motor_asyncio
import os

from typing import Optional, List

def create_mongo_pool():
  global client
  client = motor.motor_asyncio.AsyncIOMotorClient(
    f"mongodb://{os.environ['DB_HOST']}:{os.environ['DB_PORT']}?retryWrites=true&w=majority"
  )

def get_database():
  return client[os.environ["DB_NAME"]]

class MessageDAO:
  def __init__(self):
    self.__collection = get_database()['messages']
  
  async def get_chat(self, user1, user2):
    pipeline = [
      {"$match": {"$or": [
        {"$and": [{"sender": user1}, {"receiver": user2}]},
        {"$and": [{"sender": user2}, {"receiver": user1}]}
      ]}},
      {"$sort": {"timestamp": 1}}
    ]
    return await self.__collection.aggregate(pipeline).to_list(length=None)
  
  async def insert(self, sender, message_event):
    await self.__collection.insert_one({
      "sender": sender,
      "receiver": message_event["receiver"],
      "content": message_event["content"],
      "timestamp": message_event["timestamp"]
    })