package repository;

import model.Rental;
import java.sql.SQLException;
import java.util.List;

public interface AdminRentalLogsRepository {
    List<Rental> getRentalLogs() throws SQLException;
}