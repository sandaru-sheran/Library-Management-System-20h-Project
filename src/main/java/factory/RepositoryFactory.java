package factory;

import repository.*;
import repository.impl.*;

public class RepositoryFactory {

    private static RepositoryFactory instance;

    private RepositoryFactory() {}

    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    public <T> T getRepository(Class<T> repositoryType) {
        if (repositoryType == BookRepository.class) {
            return (T) new BookRepositoryImpl();
        } else if (repositoryType == UserRepository.class) {
            return (T) new UserRepositoryImpl();
        } else if (repositoryType == DashboardRepository.class) {
            return (T) new DashboardRepositoryImpl();
        } else if (repositoryType == AdminReportsRepository.class) {
            return (T) new AdminReportsRepositoryImpl();
        } else if (repositoryType == AdminOverviewRepository.class) {
            return (T) new AdminOverviewRepositoryImpl();
        } else if (repositoryType == AdminRentalLogsRepository.class) {
            return (T) new AdminRentalLogsRepositoryImpl();
        } else if (repositoryType == MemberManagementRepository.class) {
            return (T) new MemberManagementRepositoryImpl();
        } else if (repositoryType == RentalManagementRepository.class) {
            return (T) new RentalManagementRepositoryImpl();
        } else if (repositoryType == AdminBookOverviewRepository.class) {
            return (T) new AdminBookOverviewRepositoryImpl();
        } else if (repositoryType == AdminCustomerBaseRepository.class) {
            return (T) new AdminCustomerBaseRepositoryImpl();
        }
        throw new IllegalArgumentException("Unknown repository type: " + repositoryType.getName());
    }
}