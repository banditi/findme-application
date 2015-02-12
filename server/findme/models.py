from django.db import models

# Create your models here.
class User(models.Model):

  class Meta:
    verbose_name = "User"
    verbose_name_plural = "Users"

  # def __unicode__(self):
  #   return self.idvk.encode('utf-8', 'replace')

  idvk = models.IntegerField(unique=True)
  # add field: online
