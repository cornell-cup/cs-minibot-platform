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

public final class BaseInterfaceHolder extends Ice.ObjectHolderBase<BaseInterface>
{
    public
    BaseInterfaceHolder()
    {
    }

    public
    BaseInterfaceHolder(BaseInterface value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        if(v == null || v instanceof BaseInterface)
        {
            value = (BaseInterface)v;
        }
        else
        {
            IceInternal.Ex.throwUOE(type(), v);
        }
    }

    public String
    type()
    {
        return _BaseInterfaceDisp.ice_staticId();
    }
}
