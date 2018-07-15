package api.common.postfilter;

import api.websocket.post.filter.exception.MalformedParameterException;
import common.postfilter.FilterData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class FilterParameterDecoder {

//    public FilterData decode2(String parameter){
//        List<String> sepParam = Arrays.stream(parameter.split(","))
//                .filter(str -> !str.isEmpty())
//                .collect(Collectors.toList());
//
//
//
//
//    }
//
//    private ParamPair paramToPair(String param){
//        return new ParamPair(param.charAt(0), param.substring(1));
//    }
//
//    private FilterData buildData(List<ParamPair> pairs){
//        FilterData.Builder builder = new FilterData.Builder();
//
//        for (ParamPair pair : pairs) {
//            switch (pair.delim){
//                case FilterData.CHAT_PLATFORM_DELIM:
//                    builder.withChatPlatform(Integer.parseInt(pair.value));
//                    break;
//            }
//        }
//    }

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

    private class ParamPair{
        private char delim;
        private String value;

        public ParamPair(char delim, String value) {
            this.delim = delim;
            this.value = value;
        }

        public char getDelim() {
            return delim;
        }

        public String getValue() {
            return value;
        }
    }
}
