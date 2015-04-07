from django.conf.urls import patterns, url

from findme import views

urlpatterns = patterns('',
  url(r'^$', views.index, name='index'),
  url(r'^addid/(?P<idvk>\d+)/$', views.addid, name='add'),
  url(r'^addid/(?P<idvk>\d+)/(?P<maxdist>\d*\.\d+|\d+)/$', views.addid, name='add'),
  url(r'^setonline/(?P<idvk>\d+)/$', views.setstatus, 
    {'status': True}, name='online'),
  url(r'^setoffline/(?P<idvk>\d+)/$', views.setstatus, 
    {'status': False}, name='offline'),
  url(r'^setcoord/(?P<idvk>\d+)/(?P<lat>[-+]?\d*\.\d+|\d+)/(?P<lng>[-+]?\d*\.\d+|\d+)/$', views.setcoord, name='setcoord'),
  url(r'^getfriends/(?P<idvk>\d+)/(?P<maxdist>\d*\.\d+|\d+)/$', views.addid, name='getfriends'),
)