import asyncio
import websockets
import os
import socket

from endpoints.handlers import servers_handler, users_handler
from daos.redisdao import notify_online, get_online_servers, create_redis_pool, remove_serer_from_redis
from daos.mongodao import create_mongo_pool
from endpoints.shared_resources import ONLINE_SERVERS, ROUTINGS
from serverfunctions.utility import unpack

MY_IP = socket.gethostbyname(socket.gethostname())

async def handler(client_socket, path):
  # Initialization phase
  if len(path.split("/")) != 3:
    return
  [_, endpoint, client] = path.split("/")

  # Decision
  if endpoint == "servers":
    await servers_handler(client, client_socket)
  elif endpoint == "users":
    await users_handler(client, client_socket)

async def regularly_notify():
  while True:
    await notify_online(MY_IP)
    await asyncio.sleep(59)

async def gen_server():
  await create_redis_pool()
  create_mongo_pool()
  onservers = await get_online_servers()
  notify_task = asyncio.create_task(regularly_notify())
  # connect to each server
  for server in onservers:
    try:
      decoded = str(server, 'utf-8')
      if decoded == MY_IP:
        continue
      
      # connect to server and store the socket connection
      ssocket = await websockets.connect(f"ws://{decoded}:{int(os.environ['PORT'])}/servers/{MY_IP}")

      # get server's user list and store them with the routing
      sonline = unpack(await ssocket.recv())
      ROUTINGS.update({un: ssocket for un in sonline})
      asyncio.create_task(servers_handler(decoded, ssocket, True))
      print(f"Connected to {decoded}")
    except ConnectionRefusedError as cre:
      print(f"Connection to {decoded} failed: {cre}")
  await websockets.serve(handler, MY_IP, int(os.environ["PORT"]))
  print(f"Server listening on port {os.environ['PORT']}")

if __name__ == "__main__":
  try:
    main_loop = asyncio.new_event_loop()
    asyncio.set_event_loop(main_loop)
    main_loop.run_until_complete(gen_server())
    main_loop.run_forever()
  except KeyboardInterrupt as ki:
    print("\nReceived keyboard interrupt, stopping background tasks")