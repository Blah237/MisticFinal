//package edu.cornell.gdiac.mistic;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.JsonReader;
//import com.badlogic.gdx.utils.JsonValue;
//import com.badlogic.gdx.utils.JsonWriter;
//
//import java.io.Writer;
//
///**
// * Created by tbkepler on 5/16/17.
// *
// * An easy controller for easy modification of
// * the save game file that Nate wanted. It'll have a filepath to the
// * save game JSON, an easy increment function, a constructor and a
// * reset function.
// */
//public class SaveGameController {
//    /** File path to the JSON */
//    private static String SAVE_GAME_FILE = "jsons/SaveGame.json";
//    /** The reader to process the file */
//    private JsonReader jsonReader;
//    /** The JSON reader for save game */
//    private JsonValue saveRead;
//    /** Writer for save game */
//    private Json saveJson;
//
//
//    /** public contructor. if the save game file is empty,
//     * the constructor fills it with just a single value:
//     * "Unlocked":0
//     */
//    public SaveGameController() {
//        jsonReader = new JsonReader();
//        saveJson = new Json(JsonWriter.OutputType.json);
//        saveJson.setElementType(String.class,"Unlocked",Integer.class);
//        saveRead = jsonReader.parse(SAVE_GAME_FILE);
//        if (saveRead.isNull()) {
//            saveJson.writeValue("Unlocked",0);
//        }
//    }
//
//    /**
//     * getter for the save game filepath
//     *
//     * @return  Save Game filepath String
//     */
//    public String getSavePath() {
//        return SAVE_GAME_FILE;
//    }
//
//    /**
//     * Increment the "Unlocked" value by 1
//     */
//    public void increment() {
//        if (saveRead.isNull()|saveRead.get("Unlocked")==null) {
//            Gdx.app.error("SaveGameController",
//                    "Save game JSON file is either empty or does not have an \"Unlocked\" value",
//                    new IllegalStateException());
//            return;
//        }
//        int inc = saveRead.get("Unlocked").asInt()+1;
//        saveRead.set();
//    }
//
//}
