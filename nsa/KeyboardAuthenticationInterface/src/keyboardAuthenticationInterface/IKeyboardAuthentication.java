/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: IKeyboardAuthentication.aidl
 */
package keyboardAuthenticationInterface;
public interface IKeyboardAuthentication extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements keyboardAuthenticationInterface.IKeyboardAuthentication
{
private static final java.lang.String DESCRIPTOR = "keyboardAuthenticationInterface.IKeyboardAuthentication";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an keyboardAuthenticationInterface.IKeyboardAuthentication interface,
 * generating a proxy if needed.
 */
public static keyboardAuthenticationInterface.IKeyboardAuthentication asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof keyboardAuthenticationInterface.IKeyboardAuthentication))) {
return ((keyboardAuthenticationInterface.IKeyboardAuthentication)iin);
}
return new keyboardAuthenticationInterface.IKeyboardAuthentication.Stub.Proxy(obj);
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
case TRANSACTION_isNewResultAvailable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isNewResultAvailable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_receiveResult:
{
data.enforceInterface(DESCRIPTOR);
double _result = this.receiveResult();
reply.writeNoException();
reply.writeDouble(_result);
return true;
}
case TRANSACTION_sendData:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
this.sendData(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements keyboardAuthenticationInterface.IKeyboardAuthentication
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
@Override public boolean isNewResultAvailable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isNewResultAvailable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public double receiveResult() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
double _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_receiveResult, _data, _reply, 0);
_reply.readException();
_result = _reply.readDouble();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void sendData(double result) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(result);
mRemote.transact(Stub.TRANSACTION_sendData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_isNewResultAvailable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_receiveResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_sendData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public boolean isNewResultAvailable() throws android.os.RemoteException;
public double receiveResult() throws android.os.RemoteException;
public void sendData(double result) throws android.os.RemoteException;
}
