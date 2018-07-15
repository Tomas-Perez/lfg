package api.common.postfilter;

import api.websocket.post.filter.exception.MalformedParameterException;
import common.postfilter.FilterData;

import java.util.Arrays;

/**
 * @author Tomas Perez Molina
 */
public class FilterParameterDecoder {

    public FilterData decode(String parameter){
        String[] separatedParam = Arrays.stream(parameter.split(":"))
                .filter(str -> str.length() > 0)
                .toArray(String[]::new);

        switch (separatedParam.length){
            case 0:
                return FilterData.emptyPair();
            case 1:
                return justGamePair(separatedParam[0]);
            case 2:
                return fullPair(separatedParam[0], separatedParam[1]);
            default:
                throw new MalformedParameterException("Filter parameter is malformed");
        }
    }

    private FilterData justGamePair(String gameIDStr){
        try {
            Integer id = Integer.parseInt(gameIDStr);
            return new FilterData(id);
        } catch (NumberFormatException exc) {
            throw new MalformedParameterException("Filter parameter is malformed");
        }
    }

    private FilterData fullPair(String gameIDStr, String activityStr){
        try {
            Integer gameID = Integer.parseInt(gameIDStr);
            Integer activityID = Integer.parseInt(activityStr);
            return new FilterData(gameID, activityID);
        } catch (NumberFormatException exc) {
            throw new MalformedParameterException("Filter parameter is malformed");
        }
    }
}
