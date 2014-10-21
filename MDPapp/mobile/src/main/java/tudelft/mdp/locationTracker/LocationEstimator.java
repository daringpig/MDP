package tudelft.mdp.locationTracker;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tudelft.mdp.backend.endpoints.radioMapFingerprintEndpoint.model.ApGaussianRecord;
import tudelft.mdp.utils.Gaussian;
import tudelft.mdp.utils.Utils;

/**
 * This class estimates de current user's location according to the read network scans
 */
public class LocationEstimator {


    private ArrayList<ArrayList<NetworkInfoObject>> mNetworkScansRaw = new ArrayList<ArrayList<NetworkInfoObject>>();
    private ArrayList<ApGaussianRecord> mGaussianRecords = new ArrayList<ApGaussianRecord>();


    //Used for Bayessian
    private ArrayList<NetworkInfoObject> mNetworkScans = new ArrayList<NetworkInfoObject>();
    private HashMap<String,Double> pmf = new HashMap<String, Double>();
    private ArrayList<HashMap<String,Double>> pmfIntermediateResults = new ArrayList<HashMap<String, Double>>();
    private String currentPlace;


    // Constructors ********************************************************************************

    public LocationEstimator() {
    }

    public LocationEstimator(
            ArrayList<ArrayList<NetworkInfoObject>> networkScans,
            ArrayList<ApGaussianRecord> gaussianRecords) {
        mNetworkScansRaw = networkScans;
        mGaussianRecords = gaussianRecords;
    }


    // Getters & Setters ***************************************************************************

    public ArrayList<ArrayList<NetworkInfoObject>> getNetworkScans() {
        return mNetworkScansRaw;
    }

    public void setNetworkScans(ArrayList<ArrayList<NetworkInfoObject>> networkScans) {
        mNetworkScansRaw = networkScans;
    }

    public ArrayList<ApGaussianRecord> getGaussianRecords() {
        return mGaussianRecords;
    }

    public void setGaussianRecords(ArrayList<ApGaussianRecord> gaussianRecords) {
        mGaussianRecords = gaussianRecords;
    }


    // Bayessian ***********************************************************************************

    /**
     * calculateLocationBayessian
     * @return PMF with the probability to be in each zone (room) from the current place.
     * Ordered in descending order.
     */
    public HashMap<String,Double> calculateLocationBayessian(){
        consolidateNetworkScans();
        sortNetworksByRSSI();
        String currentPlace = determineCurrentPlace();
        if (currentPlace != null) {
            determineExistingZones();
            setInitialBelief();
            calculatePMFofZones();
            return getPmfWithHighestProbability();
        } else {
            return null;
        }
    }

    /**
     * calculateLocationBayessian_IntermediatePMFs
     * @return ArrayList with all th intermediate pmf results after calculating the probabilities
     * with each sensed network.
     */
    public ArrayList<HashMap<String,Double>> calculateLocationBayessian_IntermediatePMFs(){
        consolidateNetworkScans();
        sortNetworksByRSSI();
        String currentPlace = determineCurrentPlace();
        if (currentPlace != null) {
            determineExistingZones();
            setInitialBelief();
            calculatePMFofZones();
            return pmfIntermediateResults;
        } else {
            return null;
        }
    }


    /**
     * Consolidates all network scans into a single one.
     * It calculates the average of all readings for each different network.
     */
    public void consolidateNetworkScans(){

        for (ArrayList<NetworkInfoObject> networkScanArray : mNetworkScansRaw){

            for(NetworkInfoObject networkInfoObject : networkScanArray){

                //Check if it already exists in the consolidated array
                int index = getNetworkIndexInConsolidatedArray(networkInfoObject);

                if (index == -1){
                    NetworkInfoObject newNetworkInfoObject = new NetworkInfoObject();
                    newNetworkInfoObject.setBSSID(networkInfoObject.getBSSID());
                    newNetworkInfoObject.setSSID(networkInfoObject.getSSID());
                    networkInfoObject.setCount(1);
                    networkInfoObject.addRSSI(networkInfoObject.getRSSI());
                } else {
                    mNetworkScans.get(index).setCount(mNetworkScans.get(index).getCount() +  1);
                    mNetworkScans.get(index).addRSSI(networkInfoObject.getRSSI());
                }

            }
        }

        for (NetworkInfoObject networkInfoObject : mNetworkScans){
            networkInfoObject.setMean(Utils.getMean(networkInfoObject.getRSSIarray()));
            networkInfoObject.setStd(Utils.getStd(networkInfoObject.getRSSIarray()));
            networkInfoObject.setRSSI(networkInfoObject.getMean());
        }

    }

    /**
     * getNetworkIndexInConsolidatedArray
     * @param networkInfoObjectToCheck Network Info object to check
     * @return index in the consolidated array. -1 if it does not exist yet.
     */
    private int getNetworkIndexInConsolidatedArray(NetworkInfoObject networkInfoObjectToCheck){
        int index = -1;
        for (int i = 0; i < mNetworkScans.size(); i++){
            if (mNetworkScans.get(i).getBSSID().equals(networkInfoObjectToCheck.getBSSID())){
                return i;
            }
        }
        return index;
    }

    /**
     * Sorts the networks according to the perceived RSSI levels.
     */
    public void sortNetworksByRSSI(){
        Collections.sort(mNetworkScans, new Comparator<NetworkInfoObject>() {
            @Override
            public int compare(NetworkInfoObject item1, NetworkInfoObject item2) {

                return item2.getRSSI().compareTo(item1.getRSSI());
            }
        });
    }

    /**
     * Identifies the current place (Home/Office) in which the user is.
     * Calculates it by comparing the read scans with th Gaussians information which contains data
     * about which network belongs to which place.
     * @return Current Place
     */
    public String determineCurrentPlace(){
        for (NetworkInfoObject scannedNetwork : mNetworkScans){
            for (ApGaussianRecord apGaussianRecord : mGaussianRecords){
                if (scannedNetwork.getBSSID().equals(apGaussianRecord.getBssid())){
                    //This apGaussianRecord hold information from this network
                    currentPlace = apGaussianRecord.getPlace();
                    return apGaussianRecord.getPlace();
                }
            }
        }

        //You are in an unknown place
        return null;
    }


    /**
     * Determines the existing known zones where the user may be located.
     * Identifies the zones by analyzing which zone info is contained in the Gaussians ArrayList
     * since these are the ones that were fingerprinted.
     */
    public void determineExistingZones(){
        pmf.clear();
        pmfIntermediateResults.clear();

        String currentPlace = determineCurrentPlace();
        if (currentPlace != null) {
            for (ApGaussianRecord apGaussianRecord : mGaussianRecords) {
                if (apGaussianRecord.getPlace().equals(currentPlace)) {
                    pmf.put(apGaussianRecord.getZone(), 0.0);
                }
            }
        }
    }

    /**
     * Initializes all zones with equal probability (Uniform distribution).
     */
    public void setInitialBelief(){
        Double numberOfZones = (double) pmf.size();
        if (pmf.size() > 0) {
            Double initialProbability = 1.0 / numberOfZones;

            for (String zone : pmf.keySet()) {
                pmf.put(zone, initialProbability);
            }
        }
    }


    /**
     * Calculates the PMF of all the zones in the current place and
     * saves all intermediate results obtained after calculating the pmf
     * with each scanned network.
     */
    public void calculatePMFofZones(){

        //Use each scanned network to calculate the probabilities of the zones (rooms)
        for (NetworkInfoObject scannedNetwork : mNetworkScans){
            String networkBSSID = scannedNetwork.getBSSID();
            Double networkObservedRSSI = scannedNetwork.getRSSI();
            double probabilityOfAllZones = 0.0;

            //For each zone (room) calculate its probability
            for (String zone : pmf.keySet()){
                ApGaussianRecord apGaussianRecord = getApGaussianRecordOfNetworkInZone(networkBSSID, currentPlace, zone);
                if (apGaussianRecord != null){
                    double NetworkMeanInZone = apGaussianRecord.getMean();
                    double NetworkStdInZone = apGaussianRecord.getStd();

                    double cdf1 = Gaussian.Phi(networkObservedRSSI + 0.5, NetworkMeanInZone, NetworkStdInZone);
                    double cdf2 = Gaussian.Phi(networkObservedRSSI - 0.5, NetworkMeanInZone, NetworkStdInZone);
                    double zoneAPpmf = cdf1 - cdf2;

                    double prior = pmf.get(zone);
                    double posterior = prior * zoneAPpmf;
                    pmf.put(zone, posterior);

                    probabilityOfAllZones += posterior;

                } else {
                    //This network has never been seen in this zone (room).
                    //Still, we asign it a very low probability so it does not go to completely 0.
                    pmf.put(zone, 0.01);
                    probabilityOfAllZones += 0.01;
                }
            }

            //Normalize
            normalizePMF(probabilityOfAllZones);

            //Save the intermediate result
            pmfIntermediateResults.add(pmf);

        }

    }

    /**
     * Normalizes the PMF calculated
     */
    public void normalizePMF(double probabilityOfAllZones){
        for (String zone : pmf.keySet()){
            double normalizedProbability = pmf.get(zone) / probabilityOfAllZones;
            pmf.put(zone, normalizedProbability);
        }
    }



    /**
     * getApGaussianRecordOfNetworkInZone
     * @param networkBSSID BSSID of the network
     * @param currentPlace place
     * @param zone zone
     * @return ApGaussianRecord of the given particular network in the required zone and place.
     */
    public ApGaussianRecord getApGaussianRecordOfNetworkInZone(String networkBSSID, String currentPlace, String zone){

        for (ApGaussianRecord apGaussianRecord : mGaussianRecords){
            if (apGaussianRecord.getBssid().equals(networkBSSID) &&
                    apGaussianRecord.getPlace().equals(currentPlace) &&
                    apGaussianRecord.getZone().equals(zone)){
                return apGaussianRecord;
            }
        }

        return null;
    }


    /**
     * getPmfWithHighestProbability.
     * @return Returns the pmf intermediate result where the highest probability was found.
     */
    public HashMap<String,Double> getPmfWithHighestProbability(){
        double max = 0.0;
        int index = -1;

        for (int i = 0; i < pmfIntermediateResults.size(); i++){
            HashMap<String,Double> intermediatePMFresult = pmfIntermediateResults.get(i);
            for (Double probability : intermediatePMFresult.values()){
                if (probability > max){
                    max = probability;
                    index = i;
                }
            }
        }

        if (index > -1){
            return sortByProbability(pmfIntermediateResults.get(index));
        } else {
            return null;
        }
    }

    /**
     * sortByProbability
     * @param unsortedMap to be sorted
     * @return map with values sorted in descending order.
     */
    public static HashMap<String, Double> sortByProbability(HashMap<String, Double> unsortedMap) {
        // Convert Map to List
        List<Map.Entry<String, Double>> list =
                new LinkedList<Map.Entry<String, Double>>(unsortedMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }



}
