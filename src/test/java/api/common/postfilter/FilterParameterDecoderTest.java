package api.common.postfilter;

import common.postfilter.FilterData;
import org.junit.Test;


import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;



/**
 * @author Tomas Perez Molina
 */
public class FilterParameterDecoderTest {

    private static final FilterParameterDecoder decoder = new FilterParameterDecoder();

    @Test
    public void decodeActivity(){
        int gameID = 45;
        int activityID = 5;

        FilterData actual = decoder.decode2(String.format("%c%d:%d", FilterData.ACTIVITY_DELIM, gameID, activityID));
        FilterData expected = new FilterData.Builder().withActivity(gameID, activityID).build();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeGame(){
        int gameID = 45;
        FilterData actual = decoder.decode2(String.format("%c%d", FilterData.ACTIVITY_DELIM, gameID));
        FilterData expected = new FilterData.Builder().withGame(gameID).build();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeChatPlatform(){
        int chatPlatformID = 45;

        FilterData actual = decoder.decode2(String.format("%c%d", FilterData.CHAT_PLATFORM_DELIM, chatPlatformID));
        FilterData expected = new FilterData.Builder().withChatPlatform(chatPlatformID).build();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeGamePlatform(){
        int gamePlatformID = 45;

        FilterData actual = decoder.decode2(String.format("%c%d", FilterData.GAME_PLATFORM_DELIM, gamePlatformID));
        FilterData expected = new FilterData.Builder().withGamePlatform(gamePlatformID).build();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeType(){
        FilterData.PostType lfg = FilterData.PostType.LFG;
        FilterData.PostType lfm = FilterData.PostType.LFM;

        FilterData actual = decoder.decode2(String.format("%c%s", FilterData.TYPE_DELIM, lfg.toString()));
        FilterData expected = new FilterData.Builder().withType(lfg).build();

        assertThat(actual, is(expected));

        FilterData actual2 = decoder.decode2(String.format("%c%s", FilterData.TYPE_DELIM, lfm.toString()));
        FilterData expected2 = new FilterData.Builder().withType(lfm).build();

        assertThat(actual2, is(expected2));
    }

    @Test
    public void decodeEmpty(){
        FilterData actual = decoder.decode2("");
        FilterData expected = FilterData.emptyPair();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeWrongParam(){
        FilterData actual = decoder.decode2(String.format("%c%c5", FilterData.ACTIVITY_DELIM, FilterData.TYPE_DELIM));
        FilterData expected = FilterData.emptyPair();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeCommas(){
        FilterData actual = decoder.decode2(",,");
        FilterData expected = FilterData.emptyPair();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeFull(){
        int gameID = 5;
        int activityID = 4;
        int gamePlatformID = 68;
        int chatPlatformID = 12;
        FilterData.PostType type = FilterData.PostType.LFG;
        FilterData actual = decoder.decode2(
                String.format(
                        "%c%d:%d,%c%d,%c%d,%c%s",
                        FilterData.ACTIVITY_DELIM,
                        gameID,
                        activityID,
                        FilterData.GAME_PLATFORM_DELIM,
                        gamePlatformID,
                        FilterData.CHAT_PLATFORM_DELIM,
                        chatPlatformID,
                        FilterData.TYPE_DELIM,
                        type.toString()
                )
        );
        FilterData expected = new FilterData.Builder()
                .withType(type)
                .withChatPlatform(chatPlatformID)
                .withGamePlatform(gamePlatformID)
                .withActivity(gameID, activityID)
                .build();

        assertThat(actual, is(expected));
    }

    @Test
    public void decodeAllPossibilities(){
        int gameID = 5;
        int activityID = 4;
        int gamePlatformID = 68;
        int chatPlatformID = 12;
        FilterData.PostType type = FilterData.PostType.LFG;
        FilterData expected = new FilterData.Builder()
                .withType(type)
                .withChatPlatform(chatPlatformID)
                .withGamePlatform(gamePlatformID)
                .withActivity(gameID, activityID)
                .build();
        String activityParam = String.format("%c%d:%d", FilterData.ACTIVITY_DELIM, gameID, activityID);
        String gamePlatformParam = String.format("%c%d", FilterData.GAME_PLATFORM_DELIM, gamePlatformID);
        String chatPlatformParam = String.format("%c%d", FilterData.CHAT_PLATFORM_DELIM, chatPlatformID);
        String typeParam = String.format("%c%s", FilterData.TYPE_DELIM, type.toString());

        Permutations.of(Arrays.asList(activityParam, gamePlatformParam, chatPlatformParam, typeParam))
                .map(stream -> stream.collect(Collectors.joining(","))).map(decoder::decode2)
                .forEach(data -> assertThat(data, is(expected)));
    }
}