package io.left.core.utils.lib.mesh;

/*
*  ****************************************************************************
*  * Created by : Sudipta K Paik on 25-Aug-17 at 4:02 PM.
*  * Email : sudipta@w3engineers.com
*  *
*  * Responsibility: This is the  interface for check the state of MeshPeer
*  *
*  * Last edited by : Sudipta on 31-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/
public interface MeshPeerState {
    int ADDED = 1;
    int REMOVED = 2;
    int UPDATED = 3;
    int SUCCESS = 4;
    int FAILURE = 5;
}