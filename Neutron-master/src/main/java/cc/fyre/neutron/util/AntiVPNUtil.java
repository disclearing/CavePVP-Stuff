package cc.fyre.neutron.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AntiVPNUtil {

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final JsonParser JSON_PARSER = new JsonParser();

//    static {
//        CLIENT.setCosnnectTimeout(2, TimeUnit.SECONDS);
//        CLIENT.setReadTimeout(2, TimeUnit.SECONDS);
//        CLIENT.setWriteTimeout(2, TimeUnit.SECONDS);
//    }

    public static Result getResult(String ip) throws IOException {
        Request request = new Request.Builder()
                .url("https://beta.iprisk.info/v1/" + ip)
                .build();

        Response response = CLIENT.newCall(request).execute();

        if (response.code() != 200) {
            throw new IOException("API returned status code " + response.code() + ":\n" + response.body().string());
        }

        // Could probably use Gson to directly create the Result object
        JsonObject object = JSON_PARSER.parse(response.body().string()).getAsJsonObject();

        boolean dataCenter = object.get("data_center").getAsBoolean();
        boolean publicProxy = object.get("public_proxy").getAsBoolean();
        boolean torExitRelay = object.get("tor_exit_relay").getAsBoolean();

        String country = getOrUnknown(object.get("country_name"));
        String asn = getOrUnknown(object.get("autonomous_system_number"));
        String org = getOrUnknown(object.get("autonomous_system_organization"));

        return new Result(dataCenter || publicProxy || torExitRelay, country, asn, org);
    }

    private static String getOrUnknown(JsonElement element) {
        return element == null ? "Unknown" : element.getAsString();
    }

    @AllArgsConstructor
    @Getter
    public static final class Result {
        private final boolean bad;
        private final String country;
        private final String asn;
        private final String org;
    }
}
