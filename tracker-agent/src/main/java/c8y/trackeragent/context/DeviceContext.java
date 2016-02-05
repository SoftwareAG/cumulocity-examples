package c8y.trackeragent.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceContext {

    private final Map<String, Object> content = new ConcurrentHashMap<String, Object>();

    public Object get(String key) {
        return content.get(key);
    }

    public void put(String key, Object value) {
        content.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <K> K get(Class<K> clazz) {
        return (K) content.get(clazz.getName());
    }

    public void put(Object value) {
        content.put(value.getClass().getName(), value);
    }

    @Override
    public String toString() {
        return "DeviceContext [" + content + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceContext other = (DeviceContext) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        return true;
    }
    
}
