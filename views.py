# -*- coding: utf-8 -*-
from django.shortcuts import render, render_to_response
from django.http import HttpResponse, JsonResponse
from findme.models import User
from django.views import generic
from django.views.decorators.cache import never_cache
import urllib2
import json

api_url = 'https://api.vk.com/method/'

# TODO:: landing page
def index(request):
  obj = {
    'fa': 'search-plus',
    'page_title': 'FindMe',
  }
  return render(request, 'findme/index.html', obj)

@never_cache
def addid(request, idvk, maxdist=1000.0):
  answer = {}
  if idvk != '0':
    curr_user, created = User.objects.get_or_create(idvk=idvk)
    if created:
      url = api_url + 'users.get?user_ids=' + idvk + '&v=5.28'
      response = urllib2.urlopen(url).read()
      data = json.loads(response)
      curr_user.name = data['response'][0]['first_name'] + \
        ' ' + data['response'][0]['last_name']
      curr_user.save()

    return JsonResponse(getfriends(idvk, maxdist))
  else:
    answer['error'] = 1
    answer['message'] = 'Wrong ID.'
    return JsonResponse(answer)

@never_cache
def setstatus(request, idvk, status):
  answer = {}
  try:
    user = User.objects.get(idvk=idvk)
    user.online = status
    user.save()
    answer['error'] = 0
    answer['status'] = status
  except User.DoesNotExist:
    answer['error'] = 1
    answer['message'] = 'User doesn\'t exist.'
  return JsonResponse(answer)

@never_cache
def setcoord(request, idvk, lat, lng):
  answer = {}
  try:
    user = User.objects.get(idvk=idvk)
    user.lat = lat
    user.lng = lng
    user.save()
    answer = getfriends(idvk)
    answer['error'] = 0
    answer['lat'] = lat
    answer['lng'] = lng
  except User.DoesNotExist:
    answer['error'] = 1
    answer['message'] = 'User doesn\'t exist.'
  return JsonResponse(answer)

from math import radians, cos, sin, asin, sqrt

def haversine(lat1, lng1, lat2, lng2):
  """
  Calculate the great circle distance between two points 
  on the earth (specified in decimal degrees)
  """ 
  lat1, lng1, lat2, lng2 = map(radians, [lat1, lng1, lat2, lng2])

  dlng = lng2 - lng1 
  dlat = lat2 - lat1 
  a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlng/2)**2
  c = 2 * asin(sqrt(a)) 
  r = 6371000
  return c * r

def getdist(user1, user2):
  return haversine(user1.lat, user1.lng, user2.lat, user2.lng,)

def getfriends(idvk, maxdist):
  answer = {}
  try:
    curr_user = User.objects.get(idvk=idvk)
    url = api_url + 'friends.get?user_id=' + idvk + '&v=5.28'
    response = urllib2.urlopen(url).read()
    data = json.loads(response)
    answer['error'] = 0
    answer['items'] = list()
    for user_id in data['response']['items']:
      try:
        user = User.objects.get(idvk=user_id)
      except User.DoesNotExist:
        user = None
      if user:
        if user.online:
          dist = getdist(curr_user, user)
          if True:
          # if dist <= maxdist:
            obj = {
              'idvk': user_id,
              'name': user.name,
              'dist': dist,
            }
            answer['items'].append(obj)
  except User.DoesNotExist:
    answer['error'] = 1
    answer['message'] = 'User doesn\'t exist.'
  # return JsonResponse(answer)
  return answer

