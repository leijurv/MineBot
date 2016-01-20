
import java.util.Arrays;
import net.minecraft.client.main.Main;

public class Start {
    public static void main(String[] args) {
        System.out.println(Arrays.asList(args));
        System.out.println(System.getProperty("java.library.path"));
        //System.setProperty("java.library.path", System.getProperty("user.home") + "/Dropbox/MineBot/mcp918/jars/versions/1.8.8/1.8.8-natives/");
        //System.out.println(System.getProperty("java.library.path"));
        Main.main(concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
