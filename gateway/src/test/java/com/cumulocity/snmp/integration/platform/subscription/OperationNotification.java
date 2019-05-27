package com.cumulocity.snmp.integration.platform.subscription;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import lombok.*;
import org.svenson.JSONProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OperationNotification {

    private OperationRepresentation data;
    private String realtimeAction;

    @JSONProperty(ignore = true)
    public boolean isCreateAction() {
        return "CREATE".equals(this.realtimeAction);
    }

}
