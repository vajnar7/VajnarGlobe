package com.vajnar.vajnargnss;

public interface NtripInterface
{
    void onRtcmDataPrepared(byte[] result);
    void onNtripStarted();
}
