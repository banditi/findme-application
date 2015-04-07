from django.contrib import admin
from findme.models import User

class UserAdmin(admin.ModelAdmin):
    '''
        Admin View for User
    '''
    list_display = ('name', 'idvk', 'online')
    readonly_fields = ('name', 'idvk')
    search_fields = ['name', 'idvk']

admin.site.register(User, UserAdmin)
