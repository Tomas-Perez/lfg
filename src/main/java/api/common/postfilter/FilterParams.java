package api.common.postfilter;

import common.postfilter.FilterData;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class FilterParams {
    @QueryParam("filter")
    private List<String> filterParams;

    private List<FilterData> filterData;

    public FilterParams(List<String> filterParams) {
        this.filterParams = filterParams;
    }

    public FilterParams() {}

    private static final FilterParameterDecoder decoder = new FilterParameterDecoder();

    public List<FilterData> getFilterData(){
        if(filterData == null) {
            if(hasFilters())
                filterData = filterParams.stream()
                        .map(decoder::decode)
                        .distinct()
                        .collect(Collectors.toList());
            else filterData = Collections.singletonList(FilterData.emptyPair());
        }
        return filterData;
    }

    public boolean hasFilters(){
        return filterParams != null && !filterParams.isEmpty();
    }

    @Override
    public String toString() {
        return "FilterParams{" +
                "filterParams=" + filterParams +
                '}';
    }

    public String asQueryParams(){
        return hasFilters() ?
                filterData.stream().map(FilterData::asQueryParam).collect(Collectors.joining("&")) :
                "";
    }
}
