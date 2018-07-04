package api.common.postfilter;

import api.websocket.post.filter.exception.MalformedParameterException;
import common.postfilter.FilterPair;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class FilterParameterDecoder {
    public FilterPair decode(String parameter){
        String[] separatedParam = Arrays.stream(parameter.split(":"))
                .filter(str -> str.length() > 0)
                .toArray(String[]::new);

        switch (separatedParam.length){
            case 0:
                return FilterPair.emptyPair();
            case 1:
                return justGamePair(separatedParam[0]);
            case 2:
                return fullPair(separatedParam[0], separatedParam[1]);
            default:
                throw new MalformedParameterException("Filter parameter is malformed");
        }
    }

    private FilterPair justGamePair(String gameIDStr){
        try {
            Integer id = Integer.parseInt(gameIDStr);
            return new FilterPair(id);
        } catch (NumberFormatException exc) {
            throw new MalformedParameterException("Filter parameter is malformed");
        }
    }

    private FilterPair fullPair(String gameIDStr, String activityStr){
        try {
            Integer gameID = Integer.parseInt(gameIDStr);
            Integer activityID = Integer.parseInt(activityStr);
            return new FilterPair(gameID, activityID);
        } catch (NumberFormatException exc) {
            throw new MalformedParameterException("Filter parameter is malformed");
        }
    }
}
