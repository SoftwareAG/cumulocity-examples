package c8y.mibparser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class Register {

    @NotNull
    private String name;

    @NotNull
    private String oid;

    private String description;

    private String parentOid;

    private List<String> childOids;
}
