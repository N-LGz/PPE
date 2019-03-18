package com.nemge.ppe;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnectionService {

    private AcceptThread mThreadAccept;
    private ConnectThread mThreadConnect;
    private ConnectedThread mThreadConnected;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;
    private ProgressDialog mDialog;
    private Handler handler;

    private static final String TAG = "Bluetooth connexion";
    private static final UUID appID = UUID.fromString("eb70d6fe-0a74-433b-b3ac-53b9e8e7b116");
    private static final String AppName = "PPE";
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public BluetoothConnectionService(Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mBluetoothServerSocket;

        private AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(AppName, appID);
                Log.d(TAG, "AcceptThread: Server using " + appID);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOEException " + e.getMessage());
            }

            mBluetoothServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread running");
            BluetoothSocket socket = null;

            Log.d(TAG, "run: Server socket starts...");
            while (true) {
                try {
                    socket = mBluetoothServerSocket.accept();
                    Log.d(TAG, "run: Server socket accepted connection.");
                } catch (IOException e) {
                    Log.e(TAG, "AcceptThread: IOEException " + e.getMessage());
                }

                if (socket != null) {
                    connected(socket, mDevice);
                    try {
                        mBluetoothServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

        public void cancel() {
            Log.d(TAG, "cancel: Cancelling AcceptThread...");
            try {
                mBluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOEException " + e.getMessage());
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mBluetoothSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            mDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "run: ConnectThread running");
            try {
                Log.d(TAG, "ConnectThread: Using " + appID);
                tmp = mDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: IOEException " + e.getMessage());
            }
            mBluetoothSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();
            try {
                mBluetoothSocket.connect();
                Log.d(TAG, "ConnectThread: connected.");
            } catch (IOException e) {
                try {
                    mBluetoothSocket.close();
                    Log.d(TAG, "run: closed socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "ConnectThread: IOEException " + e1.getMessage());
                }
                Log.e(TAG, "ConnectThread: IOEException " + e.getMessage());
            }
            connected(mBluetoothSocket, mDevice);
        }

        public void cancel() {
            Log.d(TAG, "run: Closing client socket.");
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: IOEException " + e.getMessage());
            }
        }
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        if (mThreadConnect != null) {
            mThreadConnect.cancel();
            mThreadConnect = null;
        }

        if(mThreadAccept == null){
            mThreadAccept = new AcceptThread();
            mThreadAccept.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: started");

        mDialog = ProgressDialog.show(mContext, "Connection Bluetooth", "Please wait...", true);
        mThreadConnect = new ConnectThread(device, uuid);
        mThreadConnect.start();
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket){
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void connected(BluetoothSocket socket, BluetoothDevice mDevice) {
        Log.d(TAG, "connected: Starting");

        mThreadConnected = new ConnectedThread(socket);
        mThreadConnected.start();
    }
}
