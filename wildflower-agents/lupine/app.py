#! /usr/bin/env python

import requests
import time
import random

class CreatureApi(object):
  """docstring for CreatureApi"""
  def __init__(self, path, location):
    super(CreatureApi, self).__init__()
    self.path = path
    r = requests.post(path + '?x=' + str(location['x']) + '&y=' + str(location['y']))
    if (r.status_code != 200):
      raise Exception('Got error code %d on posting. Check path %s' % (r.status_code, path))
    self.id = r.content

  def get_helper(self, path):
    r = requests.get(path)
    if (r.status_code != 200):
      raise Exception('Got error code %d on getting. Check path %s' % (r.status_code, path))
    return r.json()

  def post_helper(self, path, data):
    r = requests.post(path, data = data)
    if (r.status_code != 200):
      raise Exception('Got error code %d on posting. Check path %s' % (r.status_code, path))
    return r.content

  def get_creature(self):
    return self.get_helper(self.path + '/creature/' + self.id)

  def get_observations(self):
    return self.get_helper(self.path + '/creature/' + self.id + '/observation')

  def post_movement(self, movement):
    return self.post_helper(self.path + '/creature/' + self.id + '/move', movement)

class Lupine(object):
  """docstring for Lupine"""
  def __init__(self):
    super(Lupine, self).__init__()
    self.creature_api = CreatureApi('http://localhost:9090', { 'x': 100, 'y': 100 })

  def run(self):
    while True:
      movement = {
         'force': {
              'x': -1 + random.random()*2,
              'y': -1 + random.random()*2
          }
      }
      self.creature_api.post_movement(movement)
      time.sleep(5)
      # get neighbors
      # get best neighbors
      # execute action

def main():
  print "HI"
  lupine = Lupine();
  lupine.run()

if __name__ == '__main__':
  main()
