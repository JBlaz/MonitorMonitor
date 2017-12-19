import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraHacker {

    private static final Pattern PATTERN = Pattern.compile("(setup=)(\\d{7})(\\d{2})(\\d{2})(\\d{11})(.*)(camera)(\\d{6})");

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Must include a regex for the types of files wanted. IE: *.txt");
        }

        Pattern filePattern = Pattern.compile(args[0]);

        File folder = new File(System.getProperty("user.dir"));
        File[] files = folder.listFiles();
        List<File> passwordFiles = new ArrayList<>();
        for (File file : files) {
            Matcher matcher = filePattern.matcher(file.getName());
            if (matcher.matches()) {
                passwordFiles.add(file);
            }
        }

        List<NetworkInfo> passwords = new ArrayList<>(10);

        for (File file : passwordFiles) {
            String stringFile;
            try {
                stringFile = new String(Files.readAllBytes(Paths.get(file.getName())));
            } catch (IOException e) {
                System.out.println(file.getName() + " failed.");
                continue;
            }

            Matcher matcher = PATTERN.matcher(stringFile);

            while (matcher.find()) {
                int SSIDLength = Integer.parseInt(matcher.group(3));
                int passwordLength = Integer.parseInt(matcher.group(4));
                String SSIDAndPassword = matcher.group(6);
                passwords.add(new NetworkInfo(SSIDAndPassword.substring(0, SSIDLength), SSIDAndPassword.substring(SSIDLength)));
            }
        }

        System.out.println(passwords);
    }

    private static class NetworkInfo {
        String SSID;
        String password;

        NetworkInfo(String SSID, String password) {
            this.SSID = SSID;
            this.password = password;
        }

        @Override
        public String toString() {
            return "NetworkInfo{\n" +
                    "SSID='" + SSID + "\',\n" +
                    "password='" + password + "\'\n" +
                    '}';
        }
    }
}
