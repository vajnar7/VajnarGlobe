from django.core.management import BaseCommand
import xml.etree.ElementTree
from areas.models import *


class Command(BaseCommand):
    def handle(self, *args, **options):
        file = "areas1.xml"
        Command.clear_all()

        places = xml.etree.ElementTree.parse(file).getroot().find('Document').findall('Placemark')
        ts = 1
        for p in places:
            d = p.find('data')
            area_name = d.get('ko') + ',' + d.get('pid')
            area = Area.objects.create(name=area_name)
            c = p.find('Polygon').find('outerBoundaryIs').find('LinearRing').find('coordinates')
            for a in c.text.split(' '):
                c = a.split(',')
                if c is not None and len(c) == 2:
                    GeoPoint.objects.create(area=area, timestamp=ts, lon=c[0], lat=c[1])
                    ts += 1
            print(area_name)

    @staticmethod
    def clear_all():
        for o in GeoPoint.objects.all():
            o.delete()
        for o in Area.objects.all():
            o.delete()
