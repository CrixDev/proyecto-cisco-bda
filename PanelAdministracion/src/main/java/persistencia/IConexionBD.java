package persistencia;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConexionBD {
    public Connection crearConexion() throws SQLException;
}
