# -*- coding: utf-8 -*-
from django.shortcuts import render, get_object_or_404
from django.http import HttpResponse
from findme.models import User
from django.views import generic
import urllib2
import json

# TODO:: landing page
def index(request):
  # return HttpResponse("Hello, world. You're at the poll index.")
  obj = {
    'fa': 'search-plus',
    'page_title': 'FindMe',
  }
  return render(request, 'findme/index.html', obj)

def addid(request, idvk=19932596):
  output = ''
  obj, use = User.objects.get_or_create(idvk=idvk)
  url = 'https://api.vk.com/method/friends.get?user_id=' + idvk + '&v=5.28'
  response = urllib2.urlopen(url).read()
  data = json.loads(response)
  for user_id in data['response']['items']:
    try:
      user = User.objects.get(idvk=user_id)
      output += ''.join(str(user.idvk) + ' | ')
    except User.DoesNotExist:
      pass
  return HttpResponse(output)

# TODO:: offline
def offline(request, idvk=19932596):
  output = ''
  return HttpResponse(output)