package io.left.core.utils.lib.mesh;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import io.left.rightmesh.mesh.MeshManager;

/*
*  ****************************************************************************
*  * Created by : Anjan on 29-Aug-17 at 4:02 PM.
*  * Email : anjan@w3engineers.com
*  *
*  * Responsibility: EventHandlerThread handle request from MeshLibrary and provide it to MeshProvider
*  *
*  * Last edited by : Sudipta on 31-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/
public class EventHandlerThread extends HandlerThread {
    public static final int KEY_EVENT_PEER = 1;
    public static final int KEY_EVENT_DATA = 2;
    private Handler mWorkHandler;
    private MeshProvider meshProvider;

    public EventHandlerThread(String name, MeshProvider meshProvider) {
        super(name);
        this.meshProvider = meshProvider;
    }

    public void prepareHandler() {
        mWorkHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case KEY_EVENT_PEER:
                        meshProvider.processPeerChanged((MeshManager.RightMeshEvent) msg.obj);

                        break;
                    case KEY_EVENT_DATA:
                        meshProvider.processDataReceived((MeshManager.RightMeshEvent) msg.obj);
                        break;
                }
                return true;
            }
        });
    }

    public Handler obtainHandler() {
        return mWorkHandler;
    }
}