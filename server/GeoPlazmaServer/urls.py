from django.conf.urls import url
from django.contrib import admin
from rest_framework.authtoken import views as auth_views

from areas.rest import DeleteArea, ObtainAreas, UpdateGeoPoint

urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^rest/geopoint/(?P<area_name>.*?)/?$', UpdateGeoPoint.as_view(), name='GeoPoint'),
    url(r'^rest/delete/(?P<area_name>.*?)/?$', DeleteArea.as_view(), name='GeoPoint'),
    url(r'^rest/token/', auth_views.obtain_auth_token),
    url(r'^rest/area/', ObtainAreas.as_view())
]
