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

public class ImgProcData implements java.lang.Cloneable, java.io.Serializable
{
    public String barCodeID;

    public int barCodeAngle;

    public float barCodeDistance;

    public ImgProcData()
    {
        barCodeID = "";
    }

    public ImgProcData(String barCodeID, int barCodeAngle, float barCodeDistance)
    {
        this.barCodeID = barCodeID;
        this.barCodeAngle = barCodeAngle;
        this.barCodeDistance = barCodeDistance;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        ImgProcData _r = null;
        if(rhs instanceof ImgProcData)
        {
            _r = (ImgProcData)rhs;
        }

        if(_r != null)
        {
            if(barCodeID != _r.barCodeID)
            {
                if(barCodeID == null || _r.barCodeID == null || !barCodeID.equals(_r.barCodeID))
                {
                    return false;
                }
            }
            if(barCodeAngle != _r.barCodeAngle)
            {
                return false;
            }
            if(barCodeDistance != _r.barCodeDistance)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 5381;
        __h = IceInternal.HashUtil.hashAdd(__h, "::CommModule::ImgProcData");
        __h = IceInternal.HashUtil.hashAdd(__h, barCodeID);
        __h = IceInternal.HashUtil.hashAdd(__h, barCodeAngle);
        __h = IceInternal.HashUtil.hashAdd(__h, barCodeDistance);
        return __h;
    }

    public ImgProcData
    clone()
    {
        ImgProcData c = null;
        try
        {
            c = (ImgProcData)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString(barCodeID);
        __os.writeInt(barCodeAngle);
        __os.writeFloat(barCodeDistance);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        barCodeID = __is.readString();
        barCodeAngle = __is.readInt();
        barCodeDistance = __is.readFloat();
    }

    static public void
    __write(IceInternal.BasicStream __os, ImgProcData __v)
    {
        if(__v == null)
        {
            __nullMarshalValue.__write(__os);
        }
        else
        {
            __v.__write(__os);
        }
    }

    static public ImgProcData
    __read(IceInternal.BasicStream __is, ImgProcData __v)
    {
        if(__v == null)
        {
             __v = new ImgProcData();
        }
        __v.__read(__is);
        return __v;
    }
    
    private static final ImgProcData __nullMarshalValue = new ImgProcData();

    public static final long serialVersionUID = -1299909004L;
}
