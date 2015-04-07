from django.db import models

class User(models.Model):

  class Meta:
    verbose_name = "User"
    verbose_name_plural = "Users"

  def __unicode__(self):
    return self.name

  name = models.CharField(max_length=255, default='New user')
  idvk = models.IntegerField(unique=True)
  online = models.BooleanField(default=True)
  lat = models.FloatField(default=0.0)
  lng = models.FloatField(default=0.0)
