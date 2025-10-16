import json

def unpack(message):
  return json.loads(message)

def pack(message):
  return json.dumps(message)