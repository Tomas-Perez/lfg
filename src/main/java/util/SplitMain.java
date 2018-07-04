package util;

import api.websocket.post.filter.FilterParameterDecoder;

import java.util.Arrays;

/**
 * @author Tomas Perez Molina
 */
public class SplitMain {
    public static void main(String[] args) {
        FilterParameterDecoder decoder = new FilterParameterDecoder();
        String empty = "";
        String justGame = "1542";
        String gameAndActivities = "546:[12,5,6]";

//        System.out.println(Arrays.toString(empty.split(":")));
//        System.out.println(Arrays.toString(justGame.split(":")));
//        System.out.println(Arrays.toString(gameAndActivities.split(":")));
//        System.out.println(decoder.decode(justGame));
//        System.out.println(decoder.decode(gameAndActivities));
//        System.out.println(decoder.decode(empty));

        String[] filters = {justGame, gameAndActivities};
        Arrays.stream(filters).map(decoder::decode).forEach(System.out::println);

        String[] filters2 = {};
        Arrays.stream(filters2).map(decoder::decode).forEach(System.out::println);
    }
}
