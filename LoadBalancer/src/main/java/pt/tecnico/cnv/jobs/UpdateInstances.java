package pt.tecnico.cnv.jobs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import pt.tecnico.cnv.InstanceManager;
import pt.tecnico.cnv.LoadBalancer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class UpdateInstances implements Runnable {

    public static final int WAIT_MILLIS = 30000;
    LoadBalancer loadBalancer = null;
    AmazonEC2Client ec2 = null;

    public UpdateInstances(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public void run() {
        AWSCredentials credentials = null;

        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e).printStackTrace();
            System.exit(-1);
        }

        ec2 = new AmazonEC2Client(credentials);
        ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");

        this.update();
        loadBalancer.serve();
        this.updateCycle();
    }

    private void update() {
        System.out.println("[Update Instances Job] Started...");
        try {
            DescribeInstancesResult describeInstancesResult = ec2.describeInstances();
            List<Reservation> reservations = describeInstancesResult.getReservations();

            Set<Instance> instances = reservations.stream()
                    .flatMap(reservation -> reservation.getInstances().stream())
                    .filter(instance -> instance.getState().getName().equals("running"))
                    .collect(toSet());

            System.out.println("[Update Instances Job] Total instances = " + instances.size());

            InstanceManager.getInstance().updateInstances(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCycle() {
        try {
            Thread.sleep(WAIT_MILLIS);
            this.update();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.updateCycle();
        }
    }
}
