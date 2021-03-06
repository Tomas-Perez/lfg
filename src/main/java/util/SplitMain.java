package util;

import api.common.postfilter.FilterParameterDecoder;
import common.postfilter.FilterData;

import java.util.Arrays;

/**
 * @author Tomas Perez Molina
 */
public class SplitMain {
    public static void main(String[] args) {
        FilterData data = new FilterData.Builder()
                .withActivity(45, 56)
                .withChatPlatform(46)
                .withGamePlatform(10)
                .withType(FilterData.PostType.LFG)
                .build();

        System.out.println(data.asQueryParam());

        FilterData empty = FilterData.emptyPair();

        System.out.println(empty.asQueryParam());

        FilterData data2 = new FilterData.Builder()
                .withActivity(45, 56)
                .withGamePlatform(10)
                .build();

        System.out.println(data2.asQueryParam());


//        FilterParameterDecoder decoder = new FilterParameterDecoder();
//        String empty = "";
//        String justGame = "1542";
//        String gameAndActivity = "546:12";
//
////        System.out.println(Arrays.toString(empty.split(":")));
////        System.out.println(Arrays.toString(justGame.split(":")));
////        System.out.println(Arrays.toString(gameAndActivity.split(":")));
////        System.out.println(decoder.decode(justGame));
////        System.out.println(decoder.decode(gameAndActivity));
////        System.out.println(decoder.decode(empty));
//
//        String[] filters = {justGame, gameAndActivity};
//        Arrays.stream(filters).map(decoder::decode).forEach(System.out::println);
//
//        String[] filters2 = {};
//        Arrays.stream(filters2).map(decoder::decode).forEach(System.out::println);
    }
}
