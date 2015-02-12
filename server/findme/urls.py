from django.conf.urls import patterns, url

from findme import views

urlpatterns = patterns('',
  url(r'^$', views.index, name='index'),
  url(r'^addid/$', views.addid),
  url(r'^addid/(?P<idvk>\d+)/$', views.addid, name='add'),
  # url(r'^offline/(?P<idvk>\d+)/$', views.offline, name='offline'),
)