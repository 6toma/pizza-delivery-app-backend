package nl.tudelft.sem.template.commons.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.authentication.NetId;

@Data
@AllArgsConstructor
public class PricesCodesModel {

    private String netId;
    private long storeId;
    private List<Double> prices;
    private List<String> codes;

}
