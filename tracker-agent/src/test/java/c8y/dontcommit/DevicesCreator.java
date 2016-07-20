package c8y.dontcommit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cumulocity.model.Agent;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObject;

import c8y.IsDevice;

public class DevicesCreator {

    // private static final GId GROUP_ID = GId.asGId("12303");
    private static final GId GROUP_ID = GId.asGId("20582");
    private static final int THREADS_NO = 1;

    private ExecutorService executorService;
    private volatile int totalCreated = 0;
    private PlatformImpl platform;

    public static void main(String[] args) {
        new DevicesCreator().fire();
    }

    public DevicesCreator() {
        executorService = Executors.newFixedThreadPool(10);
        CumulocityCredentials credentials = CumulocityCredentials.Builder.cumulocityCredentials("sysadmin", "Iwtgsitl1")
                .withTenantId("dombiel").build();
        platform = new PlatformImpl("http://management.staging.c8y.io", credentials);
        platform.setForceInitialHost(true);
    }

    private void fire() {
        for (int i = 0; i < THREADS_NO; i++) {
            executorService.execute(new Task("task_" + i));
        }
    }


    private class Task implements Runnable {
        
        private final ManagedObject groupApi;
        private final InventoryApi inventoryApi;
        private final String taskName;
        
        public Task(String taskName) {
            this.taskName = "[" + taskName + "] ";
            inventoryApi = platform.getInventoryApi();
            groupApi = inventoryApi.getManagedObjectApi(GROUP_ID);
        }
        
        private ManagedObjectRepresentation createDevice() {
            ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
            mo.setType("TEST2");
            mo.setName("TEST");
            mo.set(new Agent());
            mo.set(new IsDevice());
            return inventoryApi.create(mo);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ManagedObjectRepresentation device = createDevice();
                    System.out.println(taskName + "Created device " + device);
                    groupApi.addChildAssets(device.getId());
                    System.out.println(taskName + "Added device to group ");
                    totalCreated++;
                    System.out.println("Total created " + totalCreated);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
