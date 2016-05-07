package pt.tecnico.cnv;

import com.amazonaws.services.ec2.model.Instance;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class InstanceManager {

    public class InstanceData {

        private Instance instance = null;
        private int requests = 0;
        private BigInteger cost = new BigInteger("0");

        public InstanceData(Instance instance) {
            this.instance = instance;
        }

        private synchronized void addRequest(BigInteger cost) {
            this.cost = this.cost.add(cost);
            this.requests++;
        }

        private synchronized void removeRequest(BigInteger cost) {
            this.cost = this.cost.subtract(cost);
            this.requests--;
        }

        public int getRequests() {
            return this.requests;
        }

        public BigInteger getCost() {
            return this.cost;
        }

    }

    private HashMap<String, InstanceData> instances = new HashMap<String, InstanceData>();

    private static InstanceManager INSTANCE = null;

    private InstanceManager() {

    }

    public static InstanceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InstanceManager();
        }
        return INSTANCE;
    }

    public void addInstance(Instance instance) {
        this.instances.put(instance.getInstanceId(), new InstanceData(instance));
    }

    public void removeInstance(String name) {
        this.instances.remove(name);
    }

    public InstanceData getInstanceData(String name) {
        InstanceData instanceData = this.instances.get(name);
        if (instanceData == null) {
            throw new RuntimeException("Instance " + name + " not present.");
        }
        return instanceData;
    }

    public void addInstanceRequest(String name, BigInteger cost) {
        this.getInstanceData(name).addRequest(cost);
    }

    public void removeInstanceRequest(String name, BigInteger cost) {
        this.getInstanceData(name).removeRequest(cost);
    }

    public void updateInstances(Set<Instance> instances) {
        Set<String> instancesNames = new HashSet<String>();

        for (Instance instance : instances) {
            instancesNames.add(instance.getInstanceId());
        }

        Set<String> keySet = this.instances.keySet();

        Set<String> removed = new HashSet<String>(keySet);
        removed.removeAll(instancesNames);

        for (String instanceName : removed) {
            System.out.println("[Instance Manager] Instance was removed: " + instanceName);
            this.removeInstance(instanceName);
        }

        for (Instance instance : instances) {
            if (this.instances.get(instance.getInstanceId()) == null) {
                System.out.println("[Instance Manager] Instance was started: " + instance.getInstanceId());
                this.addInstance(instance);
            }
        }
    }
}
