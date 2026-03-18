package service;

import dto.RentalLogDTO;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public interface AdminRentalLogsService {
    ObservableList<RentalLogDTO> getRentalLogs() throws SQLException;
    ObservableList<RentalLogDTO> filterLogs(ObservableList<RentalLogDTO> logList, String keyword);
}