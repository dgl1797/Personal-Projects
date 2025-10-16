# Responds to /users/username
import asyncio
import websockets

from serverfunctions.utility import unpack, pack
from endpoints.shared_resources import ONLINE_USERS, ONLINE_SERVERS, ROUTINGS
from daos.redisdao import remove_serer_from_redis
from daos.mongodao import MessageDAO


"""
  async def users_handler(username, client_socket):
"""
async def users_handler(username, client_socket):
  print(f"new connection enstablished for: {username}.")
  websockets.broadcast(ONLINE_USERS.values(), pack({"type": "user:state", "payload": username, "online": True}))
  websockets.broadcast(ONLINE_SERVERS.values(), pack({"type": "user:state", "payload": username, "online": True}))
  ONLINE_USERS[username] = client_socket
  message_dao = MessageDAO()

  async for message in client_socket:
    message_event = unpack(message)
    if message_event["type"] == "get:chat":
      target = message_event["target"]
      message_list = await message_dao.get_chat(username, target)
      await client_socket.send(pack({
        "type": "chat:messages", 
        "payload": {
          'target': target,
          'messages': list(map(lambda doc: {
            "sender": doc["sender"],
            "receiver": doc["receiver"],
            "content": doc["content"],
            "timestamp": doc["timestamp"]
          }, message_list))
        }
      }))
    elif message_event["type"] == "get:online":
      online_list = set(list(ONLINE_USERS.keys()) + list(ROUTINGS.keys()))
      await client_socket.send(pack({
        "type": "online:list",
        "payload": list(filter(lambda el: el != username, online_list))
      }))
    elif message_event["type"] == "send":
      await message_dao.insert(username, message_event)
      # forwarding
      receiver = message_event["receiver"]
      rsocket = ONLINE_USERS[receiver] if receiver in ONLINE_USERS else ROUTINGS[receiver] if receiver in ROUTINGS else None
      if rsocket:
        await rsocket.send(pack({
          "type": "receive", 
          "payload": {
            "sender": username, 
            "receiver": receiver, 
            "content": message_event["content"],
            "timestamp": message_event["timestamp"]
          }
        }))
      
  print(f"connection with {username} over, closing and deleting entries.")
  del ONLINE_USERS[username]
  websockets.broadcast(ONLINE_USERS.values(), pack({"type": "user:state", "payload": username, "online": False}))  
  websockets.broadcast(ONLINE_SERVERS.values(), pack({"type": "user:state", "payload": username, "online": False}))  
  return


"""
  async def servers_handler(host, socket)
"""
async def servers_handler(host, socket, as_client=False):
  # provide the server's user list to the new connection
  ONLINE_SERVERS[host] = socket
  print(f"updated server list: {list(ONLINE_SERVERS.keys())}")
  if not as_client:
    await socket.send(pack(list(ONLINE_USERS.keys())))

  # enter receive loop
  try:
    async for message in socket:
      message_event = unpack(message)
      if message_event["type"] == "user:state":
        if message_event["online"]:
          ROUTINGS[message_event["payload"]] = socket
        else:
          del ROUTINGS[message_event["payload"]]
        websockets.broadcast(ONLINE_USERS.values(), message)
      elif message_event["type"] == "receive":
        receiver = message_event["receiver"]
        rsocket = ONLINE_USERS[receiver] if receiver in ONLINE_USERS else None
        if rsocket:
          await rsocket.send(pack(message_event))

  # lost connections error handling
  except websockets.exceptions.ConnectionClosedError as connection_error:
    print(f"lost connection to host: {host}")
    # clean connection
    del ONLINE_SERVERS[host]
    to_delete = []
    for k,v in ROUTINGS.items():
      if v == socket:
        to_delete.append(k)
    for k in to_delete:
      del ROUTINGS[k]
    await remove_serer_from_redis(host)
    print(f"updated server list: {list(ONLINE_SERVERS.keys())}")

  return