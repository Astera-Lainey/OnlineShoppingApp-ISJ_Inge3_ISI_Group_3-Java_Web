package OnlineShopping.entity.repository;

import OnlineShopping.entity.AdminDashboard;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDashboardRepository {

    AdminDashboard findById(int id);
}
