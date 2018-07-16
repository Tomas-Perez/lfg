package api.common.postfilter;

import api.websocket.post.filter.exception.MalformedParameterException;
import common.postfilter.FilterData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class FilterParameterDecoder {

    private static final Logger logger = LogManager.getLogger(FilterParameterDecoder.class);


    public FilterData decode2(String parameter){
        List<ParamPair> paramPairs = Arrays.stream(parameter.split(","))
                .filter(str -> !str.isEmpty())
                .map(this::paramToPair)
                .collect(Collectors.toList());

        return buildData(paramPairs);
    }

    private ParamPair paramToPair(String param){
        return new ParamPair(param.charAt(0), param.substring(1));
    }

    private FilterData buildData(List<ParamPair> pairs){
        FilterData.Builder builder = new FilterData.Builder();

        for (ParamPair pair : pairs) {
            try {
                switch (pair.delim) {
                    case FilterData.ACTIVITY_DELIM:
                        builder.withActivityFromString(pair.value);
                        break;
                    case FilterData.CHAT_PLATFORM_DELIM:
                        builder.withChatPlatform(Integer.parseInt(pair.value));
                        break;
                    case FilterData.GAME_PLATFORM_DELIM:
                        builder.withGamePlatform(Integer.parseInt(pair.value));
                        break;
                    case FilterData.TYPE_DELIM:
                        final FilterData.PostType type = FilterData.PostType.fromString(pair.value);
                        if(type == null) throw new Exception("Invalid type");
                        builder.withType(type);
                        break;
                }
            } catch (Exception exc){
                logger.error("Problem parsing: " + pair);
                logger.error(exc.getMessage());
            }
        }

        return builder.build();
    }

    public FilterData decode(String parameter){
        String[] separatedParam = Arrays.stream(parameter.split(":"))
                .filter(str -> !str.isEmpty())
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

    private class ParamPair{
        private char delim;
        private String value;

        private ParamPair(char delim, String value) {
            this.delim = delim;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ParamPair{" +
                    "delim=" + delim +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
