package DB.interfaces.transport;

import java.util.List;

public interface IController<TypeObj> {
    /**
     * Return all the exiting IDs in the table
     * @return List of IDs
     */
    List<Integer> getAllIds();
}
