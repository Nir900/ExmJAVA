package entry.AccessControl;

public class Resource {
    protected final String id;
    protected final String content;

    public Resource(String id, String content) 
    { 
        this.id = id;
        this.content = content; 
    }

    @Override
    public String toString() 
    {
        return "Resource[" + id + "]"; 
    }
}
