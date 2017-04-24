// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.3
//
// <auto-generated>
//
// Generated from file `CommModule.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package CommModule;

public interface _SensorInterfaceOperationsNC
{
    void initializeBot(int botnum);

    void sendLaserTagData(int botnum, String laserTagDataType, int hitCount);

    void sendStreamingVideoData(int botnum, byte[] bytes, int cameraID, int numObjectsDetected, ImgProcData[] imgProcseq);

    void pokeBase(int botnum);

    void sendHallSensorData(int botnum, float frontLeft, float frontRight, float backLeft, float backRight);

    void sendImuData(int botnum, float yaw, float pitch, float roll, float ax, float ay, float az);
}
