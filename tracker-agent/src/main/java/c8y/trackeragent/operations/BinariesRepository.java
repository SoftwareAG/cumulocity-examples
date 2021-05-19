package c8y.trackeragent.operations;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.BinariesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
public class BinariesRepository {
    private final BinariesApi binariesApi;

    @Autowired
    public BinariesRepository(BinariesApi binariesApi) {
        this.binariesApi = binariesApi;
    }

    public ManagedObjectRepresentation uploadFile(ManagedObjectRepresentation container, byte[] bytes) throws SDKException {
        return this.binariesApi.uploadFile(container, bytes);
    }

    public ManagedObjectRepresentation replaceFile(GId containerId, String contentType, InputStream fileStream) throws SDKException {
        return this.binariesApi.replaceFile(containerId, contentType, fileStream);
    }

    public void deleteFile(GId containerId) throws SDKException {
        this.binariesApi.deleteFile(containerId);
    }
}
