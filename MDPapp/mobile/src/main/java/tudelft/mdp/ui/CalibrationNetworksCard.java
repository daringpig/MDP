package tudelft.mdp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import tudelft.mdp.R;
import tudelft.mdp.locationTracker.NetworkInfoObject;

/**
 * Created by t7 on 13-10-14.
 */
public class CalibrationNetworksCard extends CardWithList {
    public ArrayList<NetworkInfoObject> networks;

    private Button mButton;


    private static final String TAG = "MDP-CalibrationNetworksCard";

    public CalibrationNetworksCard(Context context){
        super(context, R.layout.card_calibration_networks);
        this.networks = new ArrayList<NetworkInfoObject>();
    }

    public CalibrationNetworksCard(Context context, ArrayList<NetworkInfoObject> networks){
        super(context, R.layout.card_calibration_networks);
        this.networks = new ArrayList<NetworkInfoObject>(networks);
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_calibration_networks_inner_item;
    }


    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {


        TextView twSsid = (TextView) convertView.findViewById(R.id.twSSID);
        TextView twBssid = (TextView) convertView.findViewById(R.id.twBSSID);
        TextView twMean = (TextView) convertView.findViewById(R.id.twMean);
        TextView twCount = (TextView) convertView.findViewById(R.id.twCount);
        TextView twStd = (TextView) convertView.findViewById(R.id.twStdDev);

        CalibrationNetworks networkObject = (CalibrationNetworks) object;

        String mean = String.format("%.2f", networkObject.getMean());
        String std = String.format("%.2f", networkObject.getStd());
        twSsid.setText(networkObject.getSSID());
        twBssid.setText(networkObject.getBSSID());
        twMean.setText(mean + " db");
        twStd.setText(std);
        twCount.setText(networkObject.getCount().toString() + "");

        return  convertView;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //It is very important call the super method!!
        super.setupInnerViewElements(parent, view);


        mButton = (Button) view.findViewById(R.id.buttonRegression);
        //Your elements
    }

    @Override
    protected CardHeader initCardHeader() {
        //Add Header
        CardHeader header = new
                CalibrationNetworksHeader(getContext(), "Scanned Networks");
        //header.setTitle("Networks"); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {
        CardExpand expand = new CalibrationNetworksExpand(getContext());
        this.addCardExpand(expand);
        this.setExpanded(true);

        setEmptyViewViewStubLayoutId(R.layout.card_empty_view);
    }

    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<ListObject>();

        for (NetworkInfoObject singleNetwork : networks){
            CalibrationNetworks network = new CalibrationNetworks(this);
            network.setBSSID(singleNetwork.getBSSID());
            network.setSSID(singleNetwork.getSSID());
            network.setMean(singleNetwork.getMean());
            network.setStd(singleNetwork.getStd());
            network.setCount(singleNetwork.getCount());
            mObjects.add(network);
        }

        return mObjects;
    }

    public void updateItems(ArrayList<NetworkInfoObject> myList) {
        initCardHeader();

        networks.clear();
        networks = new ArrayList<NetworkInfoObject>(myList);

        ArrayList<CalibrationNetworks> objs = new ArrayList<CalibrationNetworks>();
        for (NetworkInfoObject singleNetwork : myList){
            CalibrationNetworks network = new CalibrationNetworks(this);
            network.setBSSID(singleNetwork.getBSSID());
            network.setSSID(singleNetwork.getSSID());
            network.setMean(singleNetwork.getMean());
            network.setStd(singleNetwork.getStd());
            network.setCount(singleNetwork.getCount());
            objs.add(network);
        }

        getLinearListAdapter().clear();
        getLinearListAdapter().addAll(objs);
    }

    public ArrayList<NetworkInfoObject> getNetworks() {
        return networks;
    }

    public void removeFromNetworksList(CalibrationNetworks removedNetwork){
        int prevSize = networks.size();
        String bssid = removedNetwork.getBSSID();
        int index = -1;
        for (int i = 0; i < networks.size(); i++){
            NetworkInfoObject network = networks.get(i);
            if (network.getBSSID().equals(bssid)){
                index = i;
                break;
            }
        }
        if (index > -1) {
            networks.remove(index);
        }

        int postSize = networks.size();
        //Toast.makeText(getContext(), "List size: " + prevSize + " " + postSize, Toast.LENGTH_SHORT).show();
    }

    public void setNetworks(ArrayList<NetworkInfoObject> networks) {
        this.networks = networks;
    }

    public class CalibrationNetworks extends CardWithList.DefaultListObject {

        public String SSID;
        public String BSSID;
        public Double mean;
        public Double std;
        public Integer count;

        public CalibrationNetworks(Card parentCard){
            super(parentCard);
            init();
        }

        public CalibrationNetworks(Card parentCard, String SSID, String BSSID, Double mean, Double std,
                Integer count) {
            super(parentCard);
            this.SSID = SSID;
            this.BSSID = BSSID;
            this.mean = mean;
            this.count = count;
            this.std = std;
        }

        public Double getStd() {
            return std;
        }

        public void setStd(Double std) {
            this.std = std;
        }

        public String getSSID() {
            return SSID;
        }

        public void setSSID(String SSID) {
            this.SSID = SSID;
        }

        public String getBSSID() {
            return BSSID;
        }

        public void setBSSID(String BSSID) {
            this.BSSID = BSSID;
        }

        public Double getMean() {
            return mean;
        }

        public void setMean(Double mean) {
            this.mean = mean;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        private void init(){
            //OnClick Listener
            setOnItemClickListener(new CardWithList.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, CardWithList.ListObject object) {
                    int i = 0;
                    //Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new CardWithList.OnItemSwipeListener() {
                @Override
                public void onItemSwipe(CardWithList.ListObject object, boolean dismissRight) {
                    int i = 0;
                    CalibrationNetworks swipedNetwork = (CalibrationNetworks) object;
                    removeFromNetworksList(swipedNetwork);
                    //Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
