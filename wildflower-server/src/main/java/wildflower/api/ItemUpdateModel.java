package wildflower.api;

/**
 * Created by ibaker on 16/10/2016.
 */
public class ItemUpdateModel<T> {
    public enum Change {
        ADD, REMOVE, UPDATE
    }

    public Change change;
    public T item;

    public static <T> ItemUpdateModel<T> to(Change change, T item) {
        ItemUpdateModel<T> result = new ItemUpdateModel<>();
        result.change = change;
        result.item = item;
        return result;
    }
}
