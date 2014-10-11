package tudelft.mdp.communication;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import tudelft.mdp.enums.MessagesProtocol;

public class ListenerService extends WearableListenerService {


    private static final String LOGTAG = "MDP-Wear WearableListenerService";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.v(LOGTAG, "DataMap received from watch: " + dataMap);
                // Broadcast message to wearable activity for display
                Intent messageIntent = new Intent(MessagesProtocol.WEARSENSORSBUNDLE);
                messageIntent.putExtras(dataMap.toBundle());
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(MessagesProtocol.MSGPATH)) {
            final String message = new String(messageEvent.getData());

            Log.i(LOGTAG, "Message received from watch: " + message);

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent(MessagesProtocol.WEARSENSORSMSG);
            messageIntent.putExtra(MessagesProtocol.MESSAGE, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }


}
