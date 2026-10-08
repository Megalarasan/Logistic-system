package DB.interfaces.employees;

import java.util.List;

public interface IEmpController<TypeObj> {
    /**
     * Return all the exiting IDs in the table
     * @return List of IDs
     */
    List<String> getAllIds();
}
