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
// Generated from file `VisionModule.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package VisionModule;

public abstract class Callback_BaseInterface_ping
    extends IceInternal.TwowayCallback implements Ice.TwowayCallbackDouble
{
    public final void __completed(Ice.AsyncResult __result)
    {
        BaseInterfacePrxHelper.__ping_completed(this, __result);
    }
}