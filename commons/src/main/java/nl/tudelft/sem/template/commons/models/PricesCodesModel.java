package nl.tudelft.sem.template.commons.models;

import java.util.List;
import lombok.Data;

@Data
public class PricesCodesModel {

    private List<Double> prices;
    private List<String> codes;

    public PricesCodesModel(List<Double> prices, List<String> codes) {
        this.prices = prices;
        this.codes = codes;
    }
}
