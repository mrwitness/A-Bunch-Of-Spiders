package wuxian.me.zhihuspider.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.zhihuspider.model.ret.ActivityVerb;
import wuxian.me.zhihuspider.model.ret.Behavior;
import wuxian.me.zhihuspider.model.ret.Verb;
import wuxian.me.zhihuspider.model.ret.Verb2;

import java.lang.reflect.Type;

/**
 * Created by wuxian on 6/9/2017.
 */
public class CustomGson {

    private static Gson gson;

    static {

        gson = new GsonBuilder()
                .registerTypeAdapter(ActivityVerb.class, new VerbDeserializer())
                .create();
    }

    private static class VerbDeserializer implements JsonDeserializer<ActivityVerb> {

        @Override
        public ActivityVerb deserialize(JsonElement json
                , Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            ActivityVerb verb = new ActivityVerb();

            if (json.isJsonObject()) {

                JsonObject j = json.getAsJsonObject();
                String v = j.get("verb").getAsString();

                if (v != null) {
                    if (v.equals(Behavior.MEMBER_LIKE_PIN.toString())) {
                        return GsonProvider.gson().fromJson(json
                                , new TypeToken<ActivityVerb<Verb2>>() {
                                }.getType());
                    } else {
                        return GsonProvider.gson().fromJson(json
                                , new TypeToken<ActivityVerb<Verb>>() {
                                }.getType());
                    }
                }
            }

            return verb;
        }
    }

    public static Gson gson() {
        return gson;
    }
}
