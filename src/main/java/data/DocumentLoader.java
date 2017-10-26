package data;

import java.util.Map;

public interface DocumentLoader<TId> {
    Map<TId, Document<TId>> getAllGroupedById();
}
