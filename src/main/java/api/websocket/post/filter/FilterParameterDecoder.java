package api.websocket.post.filter;

import api.websocket.post.filter.exception.MalformedParameterException;

import java.util.Arrays;
import java.util.List;
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

    private FilterPair fullPair(String gameIDStr, String activitiesStr){
        try {
            Integer gameID = Integer.parseInt(gameIDStr);
            final String[] splitActivitiesStr = activitiesStr
                    .substring(1, activitiesStr.length() - 1)
                    .split(",");
            List<Integer> activities = Arrays.stream(splitActivitiesStr)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new FilterPair(gameID, activities);
        } catch (NumberFormatException exc) {
            throw new MalformedParameterException("Filter parameter is malformed");
        }
    }
}
