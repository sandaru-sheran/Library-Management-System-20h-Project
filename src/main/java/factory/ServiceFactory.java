package factory;

import service.*;
import service.impl.*;

public class ServiceFactory {

    private static ServiceFactory instance;

    private ServiceFactory() {}

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public <T> T getService(Class<T> serviceType) {
        if (serviceType == BookService.class) {
            return (T) new BookServiceImpl();
        } else if (serviceType == UserService.class) {
            return (T) new UserServiceImpl();
        } else if (serviceType == DBSetupService.class) {
            return (T) new DBSetupServiceImpl();
        } else if (serviceType == DashboardService.class) {
            return (T) new DashboardServiceImpl();
        } else if (serviceType == AdminReportsService.class) {
            return (T) new AdminReportsServiceImpl();
        } else if (serviceType == AdminOverviewService.class) {
            return (T) new AdminOverviewServiceImpl();
        } else if (serviceType == AdminRentalLogsService.class) {
            return (T) new AdminRentalLogsServiceImpl();
        } else if (serviceType == MemberManagementService.class) {
            return (T) new MemberManagementServiceImpl();
        } else if (serviceType == RentalManagementService.class) {
            return (T) new RentalManagementServiceImpl();
        } else if (serviceType == AdminBookOverviewService.class) {
            return (T) new AdminBookOverviewServiceImpl();
        } else if (serviceType == AdminCustomerBaseService.class) {
            return (T) new AdminCustomerBaseServiceImpl();
        }
        throw new IllegalArgumentException("Unknown service type: " + serviceType.getName());
    }
}