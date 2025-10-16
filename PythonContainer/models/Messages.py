from daos.mongodao import get_database

class MessageDAO:
  def __init__(self):
    self.__connection = get_database()['messages']
  
  