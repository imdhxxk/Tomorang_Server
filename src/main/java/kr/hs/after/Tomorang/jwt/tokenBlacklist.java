package kr.hs.after.Tomorang.jwt;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class tokenBlacklist {
    private Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}