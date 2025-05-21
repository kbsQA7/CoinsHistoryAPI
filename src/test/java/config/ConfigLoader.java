package config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:api.properties")
public interface ConfigLoader extends Config {
    @Key("api.baseUri")
    String baseUri();

    @Key("api.basePath")
    String basePath();

    @Key("api.key")
    String apiKey();
}