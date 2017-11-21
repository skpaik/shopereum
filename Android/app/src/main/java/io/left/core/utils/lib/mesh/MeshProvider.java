package io.left.core.utils.lib.mesh;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.MeshUtility;
import io.left.rightmesh.util.RightMeshException;
import io.reactivex.functions.Consumer;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;
import static io.left.core.utils.lib.mesh.EventHandlerThread.KEY_EVENT_DATA;
import static io.left.core.utils.lib.mesh.EventHandlerThread.KEY_EVENT_PEER;

/*
*  ****************************************************************************
*  * Created by : Sudipta K Paik on 23-Aug-17 at 4:02 PM.
*  * Email : sudipta@w3engineers.com
*  *
*  * Responsibility: This is the main class who interact with RightMesh Library.
*  * This class implement MeshStateListener interface, which get data from library
*  *
*  * Last edited by : Sudipta on 31-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/
public class MeshProvider implements MeshStateListener {
    private static final String TAG = MeshProvider.class.getCanonicalName();
    private static MeshProvider sMeshProvider = null;
    private BiMap<MeshPeer, MeshID> mMeshPeerMeshIDMap = HashBiMap.create();
    private int meshPort;
    private String mSsid;
    private Context mContext;
    private AndroidMeshManager androidMeshManager = null;
    private Callback mCallback;
    private EventHandlerThread mEventHandlerThread;
    private boolean isRunning = false;

    /**
     * protecting to avoid creation from outside of this package
     */
    private MeshProvider() {
    }

    /**
     * Singleton Implements
     *
     * @return_type: MeshCallback
     * @return_value: MeshCallback singleTon object
     */
    public static MeshProvider on() {
        if (sMeshProvider == null)
            sMeshProvider = new MeshProvider();

        return sMeshProvider;
    }


    /**
     * Init MeshProvider with required data
     *
     * @param context 1 byte for data type
     * @param port    actual data array.
     * @param ssid    actual data array.
     * @return_type: MeshCallback
     * @return_value: MeshCallback singleTon object
     */
    public MeshProvider init(Context context, int port, String ssid) {
        if (isRunning)
            return sMeshProvider;

        mContext = context;
        meshPort = port;
        mSsid = ssid;

        /**
         * to manage concurrent event queueing system
         */
        mEventHandlerThread = new EventHandlerThread("RightShareHandler", this);
        mEventHandlerThread.start();
        mEventHandlerThread.prepareHandler();

        androidMeshManager = AndroidMeshManager.getInstance(mContext, this, mSsid);

        return sMeshProvider;
    }

    /**
     * Start RightMesh Library.
     * If library already started then it will not start again
     *
     * @return_type: void
     */
    public void start() {
        try {
            if (!isRunning) {
                logData("Library start Calling");
                androidMeshManager.resume();
            }
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
            throwException(e);
        }
    }

    /**
     * setCallback for get update at caller methods.
     *
     * @param callback 1 byte for data type
     * @return_type: MeshCallback
     */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     * Stop RightMesh Library.
     * If library started then it will stop the library
     *
     * @return_type: void
     */
    public void stop() {
        try {
            if (isRunning) {
                logData("Library stop Calling");
                androidMeshManager.stop();
                androidMeshManager = null;
                mMeshPeerMeshIDMap.clear();
                sMeshProvider = null;
                isRunning = false;
            }
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
            throwException(e);
        }
    }

    /**
     * Checking if the library is currently running or not.
     *
     * @return_type: boolean
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Called by the {@link MeshService} when the mesh state changes. Initializes mesh connection
     * on first call.
     *
     * @param meshID our own user id on first detecting
     * @param state  state which indicates SUCCESS or an error code
     * @return_type: void
     */
    @Override
    public void meshStateChanged(MeshID meshID, int state) {
        if (state == MeshStateListener.SUCCESS) {
            logData("Library started");
            isRunning = true;
            try {
                logData(" Initialize successful");
                // Binds this app to MESH_PORT.
                // This app will now receive all events generated on that port.
                androidMeshManager.bind(meshPort);

                // Subscribes handlers to receive events from the mesh.
                androidMeshManager.on(DATA_RECEIVED, new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        handleDataReceived((MeshManager.RightMeshEvent) o);
                        logData("data_receive_consuumer");
                    }
                });
                androidMeshManager.on(PEER_CHANGED, new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        handlePeerChanged((MeshManager.RightMeshEvent) o);
                        logData("Peer_receive_consuumer");
                    }
                });

                // If you are using Java 8 or a lambda backport like RetroLambda, you can use
                // a more concise syntax, like the following:
                // androidMeshManager.on(PEER_CHANGED, this::handlePeerChanged);
                // androidMeshManager.on(DATA_RECEIVED, this::dataReceived);
            } catch (RightMeshException e) {
                String status = "Error initializing the library" + e.toString();
                throwException(e);

                return;
            }
        }

        if (mCallback != null) {
            mCallback.onSelfPeer(generateMeshPeer(meshID));
        }
    }

    /**
     * Handles incoming data events from the mesh - toasts the contents of the data.
     *
     * @param e event object from mesh
     * @return_type: void
     */
    private void handleDataReceived(MeshManager.RightMeshEvent e) {
        Message msg = mEventHandlerThread.obtainHandler().obtainMessage(KEY_EVENT_DATA);
        msg.obj = e;
        msg.sendToTarget();
    }

    /**
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     * @return_type: void
     */
    private void handlePeerChanged(MeshManager.RightMeshEvent e) {
        Message msg = mEventHandlerThread.obtainHandler().obtainMessage(KEY_EVENT_PEER);
        msg.obj = e;
        msg.sendToTarget();
    }

    /**
     * Receive Data from Handler
     * Process and pass Data to Callback
     *
     * @param e DataReceivedEvent from Library
     * @return_type: void
     */
    public void processDataReceived(MeshManager.RightMeshEvent e) {
        final MeshManager.DataReceivedEvent event = (MeshManager.DataReceivedEvent) e;

        if (mCallback != null) {
            logData("processDataReceived Data pass to callback");
            mCallback.onData(processMeshData(event));
        } else {
            logData("Data mMeshCallback null");
        }
    }

    /**
     * Receive Peer from Handler
     * Process and pass Peer to Callback
     *
     * @param e PeerChangedEvent from Library
     * @return_type: void
     */
    public void processPeerChanged(MeshManager.RightMeshEvent e) {
        MeshManager.PeerChangedEvent event = (MeshManager.PeerChangedEvent) e;

        if (event.peerUuid == null) return;

        MeshID meshID = event.peerUuid;

        if (event.state == REMOVED) {
            mMeshPeerMeshIDMap.remove(mMeshPeerMeshIDMap.inverse().get(meshID));
        }
        if (androidMeshManager == null) return;
        if (meshID.equals(androidMeshManager.getUuid())) {
            logData("Self peer ");
            return;
        } else {
            logData("process Peer Changed");
        }

        MeshPeer meshPeer = generateMeshPeer(meshID);

        if (meshPeer == null) return;

        meshPeer.state = event.state;

        if (mCallback != null) {
            logData("Peer processed for =" + meshPeer.getPeerId());
            mCallback.onPeer(meshPeer);
        } else {
            logData("Peer mMeshCallback null");
        }
    }

    /**
     * Checking if self Peer or not
     *
     * @return_type: boolean
     * @return_value: true/false
     */
    public boolean isSelfPeer(MeshPeer meshPeer) {

        MeshPeer selfPeer = getSelfPeer();

        if (meshPeer != null && selfPeer != null && meshPeer == getSelfPeer()) return true;

        return false;
    }

    /**
     * Get own peer
     *
     * @return_type: MeshPeer
     * @return_value: MeshPeer object
     */
    public MeshPeer getSelfPeer() {
        if (androidMeshManager != null) {
            return generateMeshPeer(androidMeshManager.getUuid());
        }

        return null;
    }

    /**
     * Show Settings
     *
     * @return_type: void
     */
    public void showSettings() {
        if (androidMeshManager != null) {
            try {
                androidMeshManager.showSettingsActivity();
            } catch (RightMeshException e) {
                logData("Service not connected");
                throwException(e);
            }
        }
    }

    /**
     * Get list of Peers
     *
     * @return_type: List<MeshPeer>
     * @return_value: MeshPeer object list
     */
    public List<MeshPeer> getPeers() {
        return new ArrayList<>(mMeshPeerMeshIDMap.keySet());
    }

    /**
     * Get count of Peers
     *
     * @return_type: int
     * @return_value: Peer count
     */
    public int getPeerCount() {
        try {
            return androidMeshManager.getPeers().size();
        } catch (RightMeshException e) {
            e.printStackTrace();
            throwException(e);
        }
        return 0;
    }

    /**
     * Checking Peer list is empty or not.
     *
     * @return_type: boolean
     * @return_value: true/false
     */
    public boolean isEmptyPeer() {
        try {
            return androidMeshManager.getPeers().isEmpty();
        } catch (RightMeshException e) {
            e.printStackTrace();
            throwException(e);
        }
        return true;
    }

    /**
     * Send data to targeted peers
     *
     * @return_type: int
     * @return_value: int value for number of peer sent
     */
    public int sendNetwork(List<MeshPeer> targetPeers, byte type, byte[] data) {
        int sendCount = 0;

        try {
            ByteBuffer byteBuffer = buildBufferData(type, data);

            for (MeshPeer meshPeer : targetPeers) {
                MeshID meshID = getMeshId(meshPeer);

                if (meshID == null || meshID.equals(androidMeshManager.getUuid()))
                    continue;

                sendCount += send(meshID, byteBuffer.array());
            }
        } catch (RightMeshException e) {
            e.printStackTrace();
            throwException(e);
        }

        return sendCount;
    }

    /**
     * Send data to all connected peers
     *
     * @return_type: int
     * @return_value: int value for number of peer sent
     */
    public int sendNetwork(byte type, byte[] data) {
        int sendCount = 0;

        try {
            ByteBuffer byteBuffer = buildBufferData(type, data);

            for (MeshID meshID : androidMeshManager.getPeers()) {
                if (meshID.equals(androidMeshManager.getUuid())) continue;

                sendCount += send(meshID, byteBuffer.array());
            }
        } catch (RightMeshException e) {
            e.printStackTrace();
            throwException(e);
        }

        return sendCount;
    }

    /**
     * Send data to targeted MeshPeer
     *
     * @return_type: int
     * @return_value: int value for successfully sent, -1 for failed
     */
    public int send(MeshPeer meshPeer, byte type, byte[] bytes) {
        MeshID target = getMeshId(meshPeer);

        if (target != null /*&& meshPeer != getSelfPeer()*/) {
            logData("Called send()");
            try {
                return send(target, buildBufferData(type, bytes).array());
            } catch (RightMeshException e) {
                throwException(e);
            }
        } else {
            logData("not call send");
        }
        return -1;
    }

    /**
     * check the MeshPeer is available or not
     *
     * @return_type: boolean
     * @return_value: true/false
     */
    public boolean isReachable(MeshPeer meshPeer) {

        boolean isReachable = false;

        MeshID meshID = getMeshId(meshPeer);

        if (meshID != null) {
            try {
                isReachable = androidMeshManager.getPeers().contains(meshID);
            } catch (RightMeshException e) {
                throwException(e);
            }
        }

        return isReachable;
    }

    public boolean isReachable(String meshId) {
        for (MeshPeer meshPeer : mMeshPeerMeshIDMap.keySet()) {
            if (!TextUtils.isEmpty(meshId) && meshId.equals(meshPeer.getPeerId()))
                return true;
        }
        return false;
    }

    /**
     * get MeshPeer based on peerId
     *
     * @return_type: MeshPeer
     * @return_value: MeshPeer object
     */
    public MeshPeer getMeshPeer(String peerId) {
        for (MeshPeer meshPeer : mMeshPeerMeshIDMap.keySet()) {
            if (meshPeer.peerId != null && meshPeer.peerId.equals(peerId)) {
                return meshPeer;
            }
        }

        return null;
    }

    /**
     * Send data to targeted MeshID
     *
     * @return_type: int
     * @return_value: int value for successfully sent, -1 for failed
     */
    private int send(MeshID target, byte[] data) throws RightMeshException {
        int status = -1;

        if (androidMeshManager != null) {
            logData("sendDataReliable() called");

            status = androidMeshManager.sendDataReliable(target, meshPort, data);
        } else {
            logData("androidMeshManager null");
        }

        return status;
    }

    /**
     * get MeshID by MeshPeer
     *
     * @return_type: MeshID
     * @return_value: MeshID object
     */
    private MeshID getMeshId(MeshPeer meshPeer) {
        return mMeshPeerMeshIDMap.get(meshPeer);
    }

    /**
     * throw Exception by callback
     *
     * @return_type: void
     */
    private void throwException(RightMeshException e) {
        if (mCallback != null) mCallback.onException(e);
    }

    /**
     * generate MeshPeer by MeshID
     *
     * @return_type: MeshPeer
     * @return_value: MeshPeer object
     */
    @NonNull
    private MeshPeer generateMeshPeer(MeshID meshID) {
        MeshPeer meshPeer = null;

        if (meshID == null) return meshPeer;

        if (mMeshPeerMeshIDMap.containsValue(meshID)) {
            meshPeer = mMeshPeerMeshIDMap.inverse().get(meshID);
        } else {
            meshPeer = new MeshPeer();
            meshPeer.peerId = meshID.toString();
            mMeshPeerMeshIDMap.put(meshPeer, meshID);
        }

        return meshPeer;
    }

    /**
     * process Mesh Data from RightMesh Library
     *
     * @return_type: MeshData
     * @return_value: MeshData object
     */
    @NonNull
    private MeshData processMeshData(MeshManager.DataReceivedEvent event) {
        MeshData meshData = new MeshData();

        meshData.type = event.data[0];

        meshData.data = Arrays.copyOfRange(event.data, 1, event.data.length);

        meshData.meshPeer = generateMeshPeer(event.peerUuid);

        return meshData;
    }

    /**
     * create ByteBuffer from data type and byte array
     *
     * @param decisionValue 1 byte for data type
     * @param dataJson      actual data array.
     * @return_type: ByteBuffer
     * @return_value: ByteBuffer object
     */

    private static ByteBuffer buildBufferData(byte decisionValue, byte[] dataJson) {
        ByteBuffer buffer = ByteBuffer.allocate(1 + dataJson.length);
        buffer.put(decisionValue);
        buffer.put(dataJson);
        return buffer;
    }


    /**
     * for log data
     */
    private void logData(String msg) {
        MeshUtility.Log(TAG, msg);
    }

    public interface Callback {
        /*on Exception*/
        void onException(Exception e);

        /*on Peer arrived with state*/
        void onPeer(MeshPeer meshPeer);

        /*on start of library get self peer*/
        void onSelfPeer(MeshPeer meshPeer);

        /*on Peer data arrived with Peer*/
        void onData(MeshData meshData);
    }
}