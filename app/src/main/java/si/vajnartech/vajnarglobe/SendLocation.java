package si.vajnartech.vajnarglobe;

import android.support.annotation.NonNull;

import static si.vajnartech.vajnarglobe.C.AREAS;

public class SendLocation extends REST<LocationObj>
{
  private Double latitude;
  private Double longitude;
  private Long timestamp;

  SendLocation(String areaName, long timestamp, double longitude, double latitude)
  {
    super(String.format(AREAS, areaName));
    this.timestamp = timestamp;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  @Override
  public LocationObj backgroundFunc()
  {
    return callServer(new LocationData(timestamp, String.valueOf(longitude), String.valueOf(latitude)), OUTPUT_TYPE_JSON);
  }

  @Override
  protected void onPostExecute(LocationObj j)
  {
    super.onPostExecute(j);
  }
}

@SuppressWarnings("WeakerAccess")
class LocationData
{
  Long timestamp;
  String lon;
  String lat;

  LocationData(Long timestamp, String longitude, String latitude)
  {
    this.timestamp = timestamp;
    this.lon = longitude;
    this.lat = latitude;
  }
}

@SuppressWarnings("WeakerAccess")
class LocationObj
{
  String return_code;

  @NonNull @Override
  public String toString()
  {
    return "LocationObj{" +
           "return_code='" + return_code + '\'' +
           "}";
  }
}