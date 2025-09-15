package entry.AccessControl;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccessControl {
    private final Map<Role, Set<String>> permissions = new EnumMap<>(Role.class);

    public AccessControl() 
    {
        for (Role r : Role.values()) 
            permissions.put(r, new HashSet<>());
    }

    public void grant(Role role, String resourceId) 
    {
        permissions.get(role).add(resourceId);
    }

    public void revoke(Role role, String resourceId) 
    {
        permissions.get(role).remove(resourceId);
    }

    public boolean canRead(User user, Resource res) 
    {
        if (user.role == Role.ADMIN)
            return true;
            
        return permissions.getOrDefault(user.role, Collections.emptySet()).contains(res.id);
    }

    private String read(User user, Resource res) throws SecurityException 
    {
        if (!canRead(user, res)) 
            throw new SecurityException("Access denied for " + user + " to " + res);

        return res.content;
    }

    public static void tryRead(User user, Resource res, AccessControl acl) 
    {
        try {
            System.out.println(user + " -> " + acl.read(user, res));
        } catch (SecurityException e) {
            System.out.println(user + " -> ACCESS DENIED");
        }
    }
}
