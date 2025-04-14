package redis.wrapper;

public class Config {
    //todo перенести в общ конфиг что надо
    public static int updateIntervalNano = 50 * 1000000;
    public static int updateIntervalMili = 50;//надо ли?
    public static String redisHost = "127.0.0.1";
    public static int redisPort = 6500;
}
