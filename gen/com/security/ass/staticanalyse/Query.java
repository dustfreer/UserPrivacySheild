/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workspace\\ASS\\src\\com\\security\\ass\\staticanalyse\\Query.aidl
 */
package com.security.ass.staticanalyse;
public interface Query extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.security.ass.staticanalyse.Query
{
private static final java.lang.String DESCRIPTOR = "com.security.ass.staticanalyse.Query";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.security.ass.staticanalyse.Query interface,
 * generating a proxy if needed.
 */
public static com.security.ass.staticanalyse.Query asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.security.ass.staticanalyse.Query))) {
return ((com.security.ass.staticanalyse.Query)iin);
}
return new com.security.ass.staticanalyse.Query.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_queryConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.queryConnect(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_queryAdUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.queryAdUrl(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_queryLocation:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.queryLocation();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_queryContact:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.queryContact();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_tel_phone:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.tel_phone();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_sendSMS:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.sendSMS();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.security.ass.staticanalyse.Query
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean queryConnect(java.lang.String ip, int port) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ip);
_data.writeInt(port);
mRemote.transact(Stub.TRANSACTION_queryConnect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean queryAdUrl(java.lang.String url) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(url);
mRemote.transact(Stub.TRANSACTION_queryAdUrl, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean queryLocation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_queryLocation, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean queryContact() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_queryContact, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean tel_phone() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_tel_phone, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean sendSMS() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_sendSMS, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_queryConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_queryAdUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_queryLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_queryContact = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_tel_phone = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_sendSMS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public boolean queryConnect(java.lang.String ip, int port) throws android.os.RemoteException;
public boolean queryAdUrl(java.lang.String url) throws android.os.RemoteException;
public boolean queryLocation() throws android.os.RemoteException;
public boolean queryContact() throws android.os.RemoteException;
public boolean tel_phone() throws android.os.RemoteException;
public boolean sendSMS() throws android.os.RemoteException;
}
