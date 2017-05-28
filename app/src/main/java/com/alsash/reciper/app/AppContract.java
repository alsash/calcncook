package com.alsash.reciper.app;

/**
 * Public keys for all elements in the Application
 */
public final class AppContract {
    private static final String TAG = "com.alsash.reciper.app.AppContract";

    // Cloud service urls
    public static final class Cloud {
        public static final int CONNECT_TIMEOUT_MS = 1000;
        public static final int READ_TIMEOUT_MS = 1000;

        public static final class Github {
            private static final String BASE_URL = "https://api.github.com/";
            private static final String ACCEPT_RAW = "application/vnd.github.v3.raw+json";
            private static final String HEADER_AGENT = "User-Agent: alsash-reciper";

            public static final class Db {
                public static final String HEADER_ACCEPT = "Accept: " + Github.ACCEPT_RAW;
                public static final String HEADER_AGENT = Github.HEADER_AGENT;
                public static final String CONFIG_ENDPOINT = "config.json";
                private static final String BASE_ENDPOINT = "repos/alsash/reciper/contents/json/";
                public static final String BASE_URL = Github.BASE_URL + BASE_ENDPOINT;
            }

        }

        public static final class Usda {
            public static final String BASE_URL = "https://ndb.nal.usda.gov/ndb/search/list/";
        }
    }

    public static final class Db {
        public static final String DATABASE_NAME = "reciper_db";
    }

    // Animation payloads
    public static final class Payload {
        public static final String FLIP_BACK_TO_FRONT = TAG + ".payload_flip_back_to_front";
        public static final String FLIP_FRONT_TO_BACK = TAG + ".payload_flip_front_to_back";
    }

    // Public entity keys
    public static final class Key {
        public static final String RECIPE_ID = TAG + ".key_recipe_id";
    }

}
