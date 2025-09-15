package entry.ChaffWinnow;

import java.util.ArrayList;
import java.util.List;

public class ChaffWinnow {
    public static List<String> chaff(String secret, List<String> fakeMessages, String tag)
    {
        List<String> mixed = new ArrayList<>(fakeMessages);
        mixed.add(secret + "|" + tag);

        return mixed;
    }

    public static List<String> winnow(List<String> mixed, String tag)
    {
        List<String> real = new ArrayList<>();
        for (String m : mixed) {
            if (m.endsWith("|" + tag))
                real.add(m.split("\\|")[0]);
        }
        
        return real;
    }
}
