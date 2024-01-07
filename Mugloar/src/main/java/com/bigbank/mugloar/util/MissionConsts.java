package com.bigbank.mugloar.util;

import com.bigbank.mugloar.util.comparator.MissionComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MissionConsts {

    public final static String SURE_THING = "Sure thing";
    public final static String PIECE_OF_CAKE = "Piece of cake";
    public final static String WALK_IN_THE_PARK = "Walk in the park";
    public final static String QUITE_LIKELY = "Quite likely";
    public final static String HMMM = "Hmmm....";
    public final static String GAMBLE = "Gamble";
    public final static String RISKY = "Risky";
    public final static String RATHER_DETRIMENTAL = "Rather detrimental";
    public final static String PLAYING_WITH_FIRE = "Playing with fire";
    public final static String SUICIDE_MISSION = "Suicide mission";
    public final static String IMPOSSIBLE = "Impossible";

    public final static String BASE64_SURE_THING = "U3VyZSB0aGluZw==";
    public final static String BASE64_WALK_IN_THE_PARK = "V2FsayBpbiB0aGUgcGFyaw==";
    public final static String BASE64_PIECE_OF_CAKE = "UGllY2Ugb2YgY2FrZQ==";
    public final static String BASE64_QUITE_LIKELY = "UXVpdGUgbGlrZWx5";
    public final static String BASE64_RATHER_DETRIMENTAL = "UmF0aGVyIGRldHJpbWVudGFs";
    public final static String BASE64_HMMM = "SG1tbS4uLi4=";
    public final static String BASE64_GAMBLE = "R2FtYmxl";
    public final static String BASE64_RISKY = "Umlza3k=";
    public final static String BASE64_PLAYING_WITH_FIRE = "UGxheWluZyB3aXRoIGZpcmU=";
    public final static String BASE64_SUICIDE_MISSION = "U3VpY2lkZSBtaXNzaW9u";
    public final static String BASE64_IMPOSSIBLE = "SW1wb3NzaWJsZQ==";


    public final static Map<String,Double> PROBABILITY_PRIORITY_MAP= createPriorityMap();
    public final static Map<String,String> ENCRYPTED_PROBABILITY_MAP= knownEncryptedProbabilites();
    public static Set<String> DANGEROUS_MISSIONS= Set.of(PLAYING_WITH_FIRE,SUICIDE_MISSION,IMPOSSIBLE,RATHER_DETRIMENTAL);
    public static Set<String> SAFE_MISSIONS= Set.of(SURE_THING,PIECE_OF_CAKE);
    public static Set<String> EASY_MISSIONS= Set.of(WALK_IN_THE_PARK, QUITE_LIKELY);
    public static Set<String> RISKY_MISSIONS= Set.of(HMMM, GAMBLE);
    public final static MissionComparator WEIGHTED_PRIORITY_COMPARATOR=new MissionComparator();

    private static Map<String, Double> createPriorityMap() {
        Map<String,Double> map = new HashMap<>(12);
        map.put(SURE_THING, 0.99);
        map.put(PIECE_OF_CAKE, 0.95);
        map.put(WALK_IN_THE_PARK, 0.84);
        map.put(QUITE_LIKELY, 0.74);
        map.put(HMMM, 0.65);
        map.put(GAMBLE, 0.54);
        map.put(RISKY, 0.45);
        map.put(RATHER_DETRIMENTAL, 0.35);
        map.put(PLAYING_WITH_FIRE, 0.24);
        map.put(SUICIDE_MISSION, 0.13);
        map.put(IMPOSSIBLE, 0.0);
        return map;
    }


    private static Map<String,String> knownEncryptedProbabilites(){
        Map<String,String> map = new HashMap<>(12);
        map.put(BASE64_SURE_THING, SURE_THING);
        map.put(BASE64_WALK_IN_THE_PARK, WALK_IN_THE_PARK);
        map.put(BASE64_PIECE_OF_CAKE, PIECE_OF_CAKE);
        map.put(BASE64_QUITE_LIKELY, QUITE_LIKELY);
        map.put(BASE64_RATHER_DETRIMENTAL, RATHER_DETRIMENTAL);
        map.put(BASE64_HMMM, HMMM);
        map.put(BASE64_GAMBLE, GAMBLE);
        map.put(BASE64_RISKY, RISKY);
        map.put(BASE64_PLAYING_WITH_FIRE, PLAYING_WITH_FIRE);
        map.put(BASE64_SUICIDE_MISSION, SUICIDE_MISSION);
        map.put(BASE64_IMPOSSIBLE, IMPOSSIBLE);
        return map;
    }
}
